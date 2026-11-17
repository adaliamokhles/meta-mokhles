# meta-mokhles

A Yocto layer that turns the Rust programs in
[price-system](https://github.com/adaliamokhles/price-system) into a bootable embedded
Linux device: **TLB Infinitum**, a gateway that puts live market prices on a CAN bus and
serves a web dashboard.

Built and tested with **Yocto Project 6.0 "Wrynose"** on `qemux86-64`.

**Three ways to try it:** [build the distribution yourself](#1-build-the-distribution-yourself)
· [watch it run](#2-watch-it-run) · [just the apps, no Yocto](#3-just-the-apps-no-yocto)

---

## What a layer is

This repository contains **no program code**. It contains *recipes*: instructions that
tell BitBake what to fetch, how to build it, and what belongs in the final image.

```
  poky            meta-openembedded        meta-mokhles
(base Linux:    (extras: can-utils,        (this layer:
 kernel, libc,   nano)                      your apps, CAN config,
 busybox)                                   images)
     └────────────────┬───────────────────────────┘
                      ▼
            bitbake mokhles-image-prices
                      ▼
     fetches price-system at a pinned commit, compiles the
     3 Rust apps, enables CAN in the kernel, packs everything
                      ▼
               an image file  ──runqemu──▶  device boots
                                                │ apps start automatically
                                                ▼
                                       port 8080 ──▶ browser
```

---

## Images

| Image | What it is |
|---|---|
| **mokhles-image** | The CAN learning image: kernel with CAN, `can-utils`, and two small C programs (`can-sender`, `can-receiver`) talking over `vcan0`. |
| **mokhles-image-prices** | The real device: the 3 Rust apps, started at boot, with the web dashboard on port 8080. |

---

## What is inside

### `recipes-kernel/linux/`
- `linux-yocto_%.bbappend` + `files/can.cfg` — a *bbappend* adds a few lines to the
  kernel recipe without copying it. `can.cfg` switches on `CONFIG_CAN`, `CONFIG_CAN_RAW`
  and `CONFIG_CAN_VCAN`, so the kernel can speak CAN at all.

### `recipes-mokhles/` — the CAN basics
| Recipe | Purpose |
|---|---|
| `can-sender` / `can-receiver` | small C programs using SocketCAN on `vcan0` |
| `can-setup` | init script that creates `vcan0` at boot |
| `hello-file` / `hello-c` | the first two recipes written while learning |
| `packagegroups/packagegroup-mokhles-can` | bundles the CAN tools, drivers and programs |
| `images/` | both image recipes |

### `recipes-prices/` — the price system
| Recipe | Purpose |
|---|---|
| `price-fetcher` | internet prices → CAN bus |
| `price-dashboard` | CAN bus → terminal |
| `price-web` | CAN bus → web page on port 8080 |
| `dns-setup` | init script writing `nameserver 10.0.2.3` (QEMU's DNS server) |
| `price-system-init` | init script starting the fetcher and the web server at boot |
| `packagegroups/packagegroup-mokhles-prices` | bundles all of the above + `kernel-module-can-raw` |

The three Rust recipes `inherit cargo`, point at the **same repository and the same
commit**, and differ only in which sub-folder they build:

```
SRC_URI = "git://github.com/adaliamokhles/price-system.git;protocol=https;branch=main"
SRCREV  = "46868cfb62d4ac6156ba35c73798579c5fe223de"   ← the exact version to build
S       = "${UNPACKDIR}/${BP}/price-web"               ← which folder of it
```

On the device the three programs always talk over **CAN**. They also carry a UDP
transport for machines that have no CAN bus, such as macOS, which is used only for
development and demos — see the
[price-system README](https://github.com/adaliamokhles/price-system). Nothing in this
layer selects it: a Yocto build targets Linux, so the CAN path is always the one
compiled in.

Each one also has a `*-crates.inc` file: the full list of Rust libraries with their
versions and checksums, so BitBake can download them itself instead of letting Cargo
reach the network during the build.

---

## 1. Build the distribution yourself

This produces the real device: a Linux system built from source, with your own kernel
configuration, and the three applications installed in it.

**Be honest with yourself about the cost first:**

| | |
|---|---|
| Host | Linux, or Windows with WSL2. **Not macOS** — Yocto cannot build there |
| Disk | **~140 GB free**. This is the Yocto Project's own figure, not an exaggeration |
| Time | hours for a first build (much less with the `-fast` configuration below) |
| RAM | it works on 6 GB; more is faster |

**Install the host packages** (Ubuntu; from the
[Yocto system requirements](https://docs.yoctoproject.org/ref-manual/system-requirements.html)):

```bash
sudo apt-get install build-essential chrpath cpio debianutils diffstat file gawk gcc git \
  iputils-ping libacl1 libcrypt-dev locales python3 python3-git python3-jinja2 \
  python3-pexpect python3-pip python3-subunit socat texinfo unzip wget xz-utils zstd
```

**Set up the build.** Everything — which release of BitBake, OpenEmbedded-Core and this
layer to use, and how they are wired together — is pinned in
[`tlb-infinitum.conf.json`](tlb-infinitum.conf.json), so there is nothing to clone or
configure by hand:

```bash
python3 -m venv ~/bitbake-setup-venv && . ~/bitbake-setup-venv/bin/activate
pip install bitbake-setup

bitbake-setup init --non-interactive \
  https://raw.githubusercontent.com/adaliamokhles/meta-mokhles/main/tlb-infinitum.conf.json \
  tlb-infinitum-fast distro/poky machine/qemux86-64
```

That clones bitbake, openembedded-core, meta-yocto, meta-openembedded and this layer at the
pinned versions, and writes the build configuration — into
`~/bitbake-builds/tlb-infinitum-wrynose`. (It may still ask you to confirm the directory
the first time.)

Drop `--non-interactive` and the trailing choices to be asked instead. The options are:

- **`tlb-infinitum-fast`** — most packages downloaded prebuilt from the Yocto sstate mirror.
  Far quicker, but needs a fast, reliable connection. Recommended.
- **`tlb-infinitum`** — everything built from source.
- **`machine/qemux86-64`** is the tested machine. `machine/qemuarm64` is offered for ARM
  hosts but has not been tested.

**Build and run:**

```bash
deactivate                      # leave the Python venv, so the build uses the system Python
source ~/bitbake-builds/tlb-infinitum-wrynose/build/init-build-env
bitbake mokhles-image-prices
runqemu mokhles-image-prices nographic slirp snapshot
```

Then open <http://localhost:8080>. Quit QEMU with `Ctrl+A`, then `X`.

- `nographic` — stay in the terminal, no QEMU window
- `slirp` — user-mode networking, so the device reaches the internet without root
- `snapshot` — run from a temporary copy of the disk; nothing written survives shutdown

The image recipe forwards the web port out of QEMU:

```
QB_SLIRP_OPT = "-netdev user,id=net0,hostfwd=tcp::8080-:8080"
```

**No login is needed.** `price-system-init` starts everything at boot, so the dashboard is
up before anyone touches the console. Logging in (as `root`, no password) is only useful
for looking inside — `candump vcan0`, or `/etc/init.d/price-system stop`.

If you prefer to add this layer to an existing Yocto build instead, it is an ordinary
layer:

```bash
bitbake-layers add-layer /path/to/meta-openembedded/meta-oe   # needed for can-utils, nano
bitbake-layers add-layer /path/to/meta-mokhles
```

---

## 2. Watch it run

<!-- Replace this with a recording: drag the file into a GitHub issue or release, then
     paste the link it gives you, as ![demo](URL) for a GIF or a bare URL for an MP4. -->

*(recording: the device booting in QEMU and the dashboard coming up — to be added)*

---

## 3. Just the apps, no Yocto

If you only want to see the dashboard, the three applications also run as ordinary
programs on any machine — macOS, Linux or Windows — in one command, with no CAN bus and
no Yocto:

```bash
git clone https://github.com/adaliamokhles/price-system.git
cd price-system
./run-demo.sh
```

That is the application layer only. None of this repository is involved. See the
[price-system README](https://github.com/adaliamokhles/price-system).

---

## Updating to a newer version of the apps

The recipes pin an exact commit, so new code in `price-system` does **not** reach the
device until that pin is moved:

```
1. merge the feature branch in price-system  →  main
2. read the new commit ID:   git log -1 --format=%H
3. put it in SRCREV in all three recipes here
4. bitbake mokhles-image-prices  +  test in QEMU
5. merge this layer's feature branch → main
```

Step 3, for all three recipes at once:

```bash
sed -i 's/<old commit id>/<new commit id>/' recipes-prices/*/*.bb
grep -r "SRCREV" recipes-prices/          # check: 3 lines, all the new ID
```

If `Cargo.toml` changed in the app (a crate added or removed), also regenerate the crate
list:

```bash
bitbake -c update_crates price-web
```

---

## Branches

`main` always builds a working image. Features are developed on their own branch and
merged by pull request once the image has booted and been tested.

---

## Depends on

`openembedded-core` (poky) and `meta-openembedded/meta-oe`.

## License

MIT

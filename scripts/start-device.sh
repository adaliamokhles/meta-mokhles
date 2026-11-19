#!/usr/bin/env bash
# Boots the TLB Infinitum device in QEMU and opens its dashboard in your browser.
#
# The image must already be built:
#     source <your build>/init-build-env
#     bitbake mokhles-image-prices
#
# Then this script can be run from anywhere. It finds the build environment by
# itself; if it guesses wrong, point it at the right one:
#     BUILD_ENV=~/bitbake-builds/tlb-infinitum-wrynose/build/init-build-env ./start-device.sh
#
# Inside the device the programs talk over a real CAN bus (vcan0), exactly as
# they would on hardware. Quit QEMU with Ctrl+A, then X.

# Note: no "set -e" here on purpose. Opening a browser from WSL returns an error
# code even when it works, and that must not kill the script.
set -uo pipefail

URL="http://localhost:8080"
IMAGE="mokhles-image-prices"

# ---------------------------------------------------------------- 1. environment
# Only needed if runqemu is not already on the PATH (i.e. you have not sourced
# init-build-env in this terminal yourself).
if ! command -v runqemu >/dev/null 2>&1; then
    ENV_SCRIPT="${BUILD_ENV:-}"

    if [ -z "$ENV_SCRIPT" ]; then
        for candidate in \
            "$HOME"/bitbake-builds/tlb-infinitum-wrynose/build/init-build-env \
            "$HOME"/bitbake-builds/*/build/init-build-env \
            "$HOME"/poky/build/init-build-env
        do
            if [ -f "$candidate" ]; then
                ENV_SCRIPT="$candidate"
                break
            fi
        done
    fi

    if [ -z "$ENV_SCRIPT" ] || [ ! -f "$ENV_SCRIPT" ]; then
        echo "Could not find a Yocto build environment."
        echo "Point this script at yours, for example:"
        echo "  BUILD_ENV=~/bitbake-builds/tlb-infinitum-wrynose/build/init-build-env $0"
        exit 1
    fi

    echo ">>> Loading build environment: $ENV_SCRIPT"
    # Yocto's own init script reads variables that may be unset, so relax the
    # "treat unset variables as errors" rule just while it runs.
    set +u
    # shellcheck disable=SC1090
    . "$ENV_SCRIPT" >/dev/null || { echo "Failed to load $ENV_SCRIPT"; exit 1; }
    set -u
fi

# ---------------------------------------------------------------- 2. browser
# Wait in the background until the device answers, then open the page.
(
    for _ in $(seq 1 90); do          # up to 90 x 2 s = 3 minutes
        if curl -s -f --max-time 3 -o /dev/null "$URL"; then
            echo ""
            echo ">>> Dashboard is ready: $URL"
            case "$(uname -s)" in
                Darwin)
                    open "$URL" >/dev/null 2>&1 || true
                    ;;
                *)
                    if grep -qi microsoft /proc/version 2>/dev/null; then
                        explorer.exe "$URL" >/dev/null 2>&1 || true   # Windows, via WSL
                    else
                        xdg-open "$URL" >/dev/null 2>&1 || true       # Linux desktop
                    fi
                    ;;
            esac
            exit 0
        fi
        sleep 2
    done
    echo ""
    echo ">>> The dashboard did not answer after 3 minutes."
    echo ">>> Log in as root on the console and check: /etc/init.d/price-system start"
) &
WATCHER=$!

# Stop the watcher whenever this script ends
trap 'kill "$WATCHER" 2>/dev/null' EXIT INT TERM

# ---------------------------------------------------------------- 3. the device
echo ">>> Booting $IMAGE (quit with Ctrl+A, then X)"
runqemu "$IMAGE" nographic slirp snapshot

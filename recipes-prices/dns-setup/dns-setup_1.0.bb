SUMMARY = "Sets the DNS server (10.0.2.3, QEMU slirp) at boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://dns-setup"
S = "${UNPACKDIR}"

inherit update-rc.d

INITSCRIPT_NAME = "dns-setup"
INITSCRIPT_PARAMS = "defaults 10"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/dns-setup ${D}${sysconfdir}/init.d/dns-setup
}

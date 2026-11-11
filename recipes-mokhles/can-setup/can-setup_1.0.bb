SUMMARY = "Creates the virtual CAN bus vcan0 automatically at boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://can-setup"
S = "${UNPACKDIR}"

inherit update-rc.d

INITSCRIPT_NAME = "can-setup"
INITSCRIPT_PARAMS = "defaults 90"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/can-setup ${D}${sysconfdir}/init.d/can-setup
}

RDEPENDS:${PN} = "iproute2 kernel-module-vcan"

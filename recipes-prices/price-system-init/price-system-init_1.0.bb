SUMMARY = "Starts price-fetcher and price-web automatically at boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://price-system"
S = "${UNPACKDIR}"

inherit update-rc.d

INITSCRIPT_NAME = "price-system"
INITSCRIPT_PARAMS = "defaults 95"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/price-system ${D}${sysconfdir}/init.d/price-system
}

RDEPENDS:${PN} = "price-fetcher price-web"

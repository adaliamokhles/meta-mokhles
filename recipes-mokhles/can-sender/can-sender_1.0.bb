SUMMARY = "Sends 5 CAN messages on vcan0, every 500 ms"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://can-sender.c"
S = "${UNPACKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} can-sender.c -o can-sender
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 can-sender ${D}${bindir}/can-sender
}

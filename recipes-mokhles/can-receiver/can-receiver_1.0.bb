SUMMARY = "Receives and interprets CAN messages on vcan0"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://can-receiver.c"
S = "${UNPACKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} can-receiver.c -o can-receiver
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 can-receiver ${D}${bindir}/can-receiver
}


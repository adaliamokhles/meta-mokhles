SUMMARY = "My first C program built with Yocto"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://hello.c"
S = "${UNPACKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} hello.c -o hello-c
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 hello-c ${D}${bindir}/hello-c
}

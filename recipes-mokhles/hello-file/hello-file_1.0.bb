SUMMARY = "Installs my hello.txt file into the image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://hello.txt"
S = "${UNPACKDIR}"

do_install() {
    install -d ${D}/home/root
    install -m 0644 ${UNPACKDIR}/hello.txt ${D}/home/root/hello.txt
}

FILES:${PN} += "/home/root/hello.txt"

SUMMARY = "Mokhles CAN package group: tools, driver, startup script and my CAN programs"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = "\
    can-utils \
    iproute2 \
    kernel-module-vcan \
    kernel-module-can-raw \
    can-setup \
    can-sender \
    can-receiver \
"

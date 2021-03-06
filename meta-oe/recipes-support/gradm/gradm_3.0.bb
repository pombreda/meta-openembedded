SUMMARY = "Administration program for the grsecurity RBAC syste"
DESCRIPTION = "\
gradm is the userspace RBAC parsing and authentication program for \
grsecurity grsecurity aims to be a complete security system. gradm \
performs several tasks for the RBAC system including authenticated \
via a password to the kernel and parsing rules to be passed to the \
kernel"
HOMEPAGE = "http://grsecurity.net/index.php"
SECTION = "admin"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4641e94ec96f98fabc56ff9cc48be14b"
DEPENDS = "flex-native bison-native ${@base_contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

SRC_URI = "http://grsecurity.net/stable/${BP}-201408301734.tar.gz \
           file://0001-Makefile-remove-strip.patch"
SRC_URI[md5sum] = "79ec912e6544c5e58753f658623763f9"
SRC_URI[sha256sum] = "b190e5afecdf3ac5020a4e5e4b698645f1c01b20d036129dd8b609c4bd0c319c"

S = "${WORKDIR}/gradm"

inherit autotools-brokensep

do_compile() {
    oe_runmake 'CC=${CC}'                               \
               'OPT_FLAGS=${CFLAGS}'                    \
               'LLEX=${STAGING_BINDIR_NATIVE}/lex'      \
               'FLEX=${STAGING_BINDIR_NATIVE}/flex'     \
               'BISON=${STAGING_BINDIR_NATIVE}/bison'   \
               ${@base_contains('DISTRO_FEATURES', 'pam', ' ', 'nopam', d)}
}

do_install() {
    oe_runmake 'CC=${CC}'                               \
               'DESTDIR=${D}'                           \
               'LLEX=${STAGING_BINDIR_NATIVE}/lex'      \
               'FLEX=${STAGING_BINDIR_NATIVE}/flex'     \
               'BISON=${STAGING_BINDIR_NATIVE}/bison'   \
               install

    # The device nodes are generated by postinstall or udev
    rm -rf ${D}/dev
}

pkg_postinst_${PN}() {
    # make sure running on the target
    if [ x"$D" != "x" ]; then
        exit 1
    fi
    /bin/mknod -m 0622 /dev/grsec c 1 13
}

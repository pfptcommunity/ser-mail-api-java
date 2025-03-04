package io.pfpt.ser.mail;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A default implementation of the IMimeMapper interface for mapping file extensions to MIME types.
 * This class provides thread-safe operations for retrieving, validating, and managing MIME type
 * mappings.
 */
public final class DefaultMimeMapper implements IMimeMapper {
  // Thread-safe map storing file extensions to MIME type mappings
  private final Map<String, String> mimeTypeMap;
  // Thread-safe set of all known MIME types
  private final Set<String> mimeTypes;
  // Synchronization lock for thread-safe operations
  private final Object lock = new Object();

  // Default MIME type used when no specific mapping is found and fallback is allowed
  private String defaultMimeType;
  // Flag to allow fallback to default MIME type if no mapping exists
  private boolean allowFallbackMimeType;
  // Flag to allow unknown MIME types not in the predefined set
  private boolean allowUnknownMimeType;

  /**
   * Constructs a DefaultMimeMapper with a predefined set of extension-to-MIME-type mappings.
   * Initializes with a default MIME type and settings for fallback and unknown MIME type handling.
   */
  public DefaultMimeMapper() {
    this.defaultMimeType = "application/octet-stream";
    this.allowFallbackMimeType = false;
    this.allowUnknownMimeType = false;

    Map<String, String> map = new ConcurrentHashMap<>();
    map.put("ez", "application/andrew-inset");
    map.put("aw", "application/applixware");
    map.put("atom", "application/atom+xml");
    map.put("atomcat", "application/atomcat+xml");
    map.put("atomsvc", "application/atomsvc+xml");
    map.put("ccxml", "application/ccxml+xml");
    map.put("cdmia", "application/cdmi-capability");
    map.put("cdmic", "application/cdmi-container");
    map.put("cdmid", "application/cdmi-domain");
    map.put("cdmio", "application/cdmi-object");
    map.put("cdmiq", "application/cdmi-queue");
    map.put("cu", "application/cu-seeme");
    map.put("davmount", "application/davmount+xml");
    map.put("dbk", "application/docbook+xml");
    map.put("dssc", "application/dssc+der");
    map.put("xdssc", "application/dssc+xml");
    map.put("ecma", "application/ecmascript");
    map.put("emma", "application/emma+xml");
    map.put("epub", "application/epub+zip");
    map.put("exi", "application/exi");
    map.put("pfr", "application/font-tdpfr");
    map.put("gml", "application/gml+xml");
    map.put("gpx", "application/gpx+xml");
    map.put("gxf", "application/gxf");
    map.put("stk", "application/hyperstudio");
    map.put("ink", "application/inkml+xml");
    map.put("inkml", "application/inkml+xml");
    map.put("ipfix", "application/ipfix");
    map.put("jar", "application/java-archive");
    map.put("ser", "application/java-serialized-object");
    map.put("class", "application/java-vm");
    map.put("json", "application/json");
    map.put("jsonml", "application/jsonml+json");
    map.put("lostxml", "application/lost+xml");
    map.put("hqx", "application/mac-binhex40");
    map.put("cpt", "application/mac-compactpro");
    map.put("mads", "application/mads+xml");
    map.put("mrc", "application/marc");
    map.put("mrcx", "application/marcxml+xml");
    map.put("ma", "application/mathematica");
    map.put("nb", "application/mathematica");
    map.put("mb", "application/mathematica");
    map.put("mathml", "application/mathml+xml");
    map.put("mbox", "application/mbox");
    map.put("mscml", "application/mediaservercontrol+xml");
    map.put("metalink", "application/metalink+xml");
    map.put("meta4", "application/metalink4+xml");
    map.put("mets", "application/mets+xml");
    map.put("mods", "application/mods+xml");
    map.put("m21", "application/mp21");
    map.put("mp21", "application/mp21");
    map.put("mp4s", "application/mp4");
    map.put("doc", "application/msword");
    map.put("dot", "application/msword");
    map.put("mxf", "application/mxf");
    map.put("bin", "application/octet-stream");
    map.put("dms", "application/octet-stream");
    map.put("lrf", "application/octet-stream");
    map.put("mar", "application/octet-stream");
    map.put("so", "application/octet-stream");
    map.put("dist", "application/octet-stream");
    map.put("distz", "application/octet-stream");
    map.put("pkg", "application/octet-stream");
    map.put("bpk", "application/octet-stream");
    map.put("dump", "application/octet-stream");
    map.put("elc", "application/octet-stream");
    map.put("deploy", "application/octet-stream");
    map.put("oda", "application/oda");
    map.put("opf", "application/oebps-package+xml");
    map.put("ogx", "application/ogg");
    map.put("omdoc", "application/omdoc+xml");
    map.put("onetoc", "application/onenote");
    map.put("onetoc2", "application/onenote");
    map.put("onetmp", "application/onenote");
    map.put("onepkg", "application/onenote");
    map.put("oxps", "application/oxps");
    map.put("xer", "application/patch-ops-error+xml");
    map.put("pdf", "application/pdf");
    map.put("pgp", "application/pgp-encrypted");
    map.put("asc", "application/pgp-signature");
    map.put("sig", "application/pgp-signature");
    map.put("prf", "application/pics-rules");
    map.put("p10", "application/pkcs10");
    map.put("p7m", "application/pkcs7-mime");
    map.put("p7c", "application/pkcs7-mime");
    map.put("p7s", "application/pkcs7-signature");
    map.put("p8", "application/pkcs8");
    map.put("ac", "application/pkix-attr-cert");
    map.put("cer", "application/pkix-cert");
    map.put("crl", "application/pkix-crl");
    map.put("pkipath", "application/pkix-pkipath");
    map.put("pki", "application/pkixcmp");
    map.put("pls", "application/pls+xml");
    map.put("ai", "application/postscript");
    map.put("eps", "application/postscript");
    map.put("ps", "application/postscript");
    map.put("cww", "application/prs.cww");
    map.put("pskcxml", "application/pskc+xml");
    map.put("rdf", "application/rdf+xml");
    map.put("rif", "application/reginfo+xml");
    map.put("rnc", "application/relax-ng-compact-syntax");
    map.put("rl", "application/resource-lists+xml");
    map.put("rld", "application/resource-lists-diff+xml");
    map.put("rs", "application/rls-services+xml");
    map.put("gbr", "application/rpki-ghostbusters");
    map.put("mft", "application/rpki-manifest");
    map.put("roa", "application/rpki-roa");
    map.put("rsd", "application/rsd+xml");
    map.put("rss", "application/rss+xml");
    map.put("rtf", "application/rtf");
    map.put("sbml", "application/sbml+xml");
    map.put("scq", "application/scvp-cv-request");
    map.put("scs", "application/scvp-cv-response");
    map.put("spq", "application/scvp-vp-request");
    map.put("spp", "application/scvp-vp-response");
    map.put("sdp", "application/sdp");
    map.put("setpay", "application/set-payment-initiation");
    map.put("setreg", "application/set-registration-initiation");
    map.put("shf", "application/shf+xml");
    map.put("smi", "application/smil+xml");
    map.put("smil", "application/smil+xml");
    map.put("rq", "application/sparql-query");
    map.put("srx", "application/sparql-results+xml");
    map.put("gram", "application/srgs");
    map.put("grxml", "application/srgs+xml");
    map.put("sru", "application/sru+xml");
    map.put("ssdl", "application/ssdl+xml");
    map.put("ssml", "application/ssml+xml");
    map.put("tei", "application/tei+xml");
    map.put("teicorpus", "application/tei+xml");
    map.put("tfi", "application/thraud+xml");
    map.put("tsd", "application/timestamped-data");
    map.put("plb", "application/vnd.3gpp.pic-bw-large");
    map.put("psb", "application/vnd.3gpp.pic-bw-small");
    map.put("pvb", "application/vnd.3gpp.pic-bw-var");
    map.put("tcap", "application/vnd.3gpp2.tcap");
    map.put("pwn", "application/vnd.3m.post-it-notes");
    map.put("aso", "application/vnd.accpac.simply.aso");
    map.put("imp", "application/vnd.accpac.simply.imp");
    map.put("acu", "application/vnd.acucobol");
    map.put("atc", "application/vnd.acucorp");
    map.put("acutc", "application/vnd.acucorp");
    map.put("air", "application/vnd.adobe.air-application-installer-package+zip");
    map.put("fcdt", "application/vnd.adobe.formscentral.fcdt");
    map.put("fxp", "application/vnd.adobe.fxp");
    map.put("fxpl", "application/vnd.adobe.fxp");
    map.put("xdp", "application/vnd.adobe.xdp+xml");
    map.put("xfdf", "application/vnd.adobe.xfdf");
    map.put("ahead", "application/vnd.ahead.space");
    map.put("azf", "application/vnd.airzip.filesecure.azf");
    map.put("azs", "application/vnd.airzip.filesecure.azs");
    map.put("azw", "application/vnd.amazon.ebook");
    map.put("acc", "application/vnd.americandynamics.acc");
    map.put("ami", "application/vnd.amiga.ami");
    map.put("apk", "application/vnd.android.package-archive");
    map.put("cii", "application/vnd.anser-web-certificate-issue-initiation");
    map.put("fti", "application/vnd.anser-web-funds-transfer-initiation");
    map.put("atx", "application/vnd.antix.game-component");
    map.put("mpkg", "application/vnd.apple.installer+xml");
    map.put("m3u8", "application/vnd.apple.mpegurl");
    map.put("swi", "application/vnd.aristanetworks.swi");
    map.put("iota", "application/vnd.astraea-software.iota");
    map.put("aep", "application/vnd.audiograph");
    map.put("mpm", "application/vnd.blueice.multipass");
    map.put("bmi", "application/vnd.bmi");
    map.put("rep", "application/vnd.businessobjects");
    map.put("cdxml", "application/vnd.chemdraw+xml");
    map.put("mmd", "application/vnd.chipnuts.karaoke-mmd");
    map.put("cdy", "application/vnd.cinderella");
    map.put("cla", "application/vnd.claymore");
    map.put("rp9", "application/vnd.cloanto.rp9");
    map.put("c4g", "application/vnd.clonk.c4group");
    map.put("c4d", "application/vnd.clonk.c4group");
    map.put("c4f", "application/vnd.clonk.c4group");
    map.put("c4p", "application/vnd.clonk.c4group");
    map.put("c4u", "application/vnd.clonk.c4group");
    map.put("c11amc", "application/vnd.cluetrust.cartomobile-config");
    map.put("c11amz", "application/vnd.cluetrust.cartomobile-config-pkg");
    map.put("csp", "application/vnd.commonspace");
    map.put("cdbcmsg", "application/vnd.contact.cmsg");
    map.put("cmc", "application/vnd.cosmocaller");
    map.put("clkx", "application/vnd.crick.clicker");
    map.put("clkk", "application/vnd.crick.clicker.keyboard");
    map.put("clkp", "application/vnd.crick.clicker.palette");
    map.put("clkt", "application/vnd.crick.clicker.template");
    map.put("clkw", "application/vnd.crick.clicker.wordbank");
    map.put("wbs", "application/vnd.criticaltools.wbs+xml");
    map.put("pml", "application/vnd.ctc-posml");
    map.put("ppd", "application/vnd.cups-ppd");
    map.put("car", "application/vnd.curl.car");
    map.put("pcurl", "application/vnd.curl.pcurl");
    map.put("dart", "application/vnd.dart");
    map.put("rdz", "application/vnd.data-vision.rdz");
    map.put("uvf", "application/vnd.dece.data");
    map.put("uvvf", "application/vnd.dece.data");
    map.put("uvd", "application/vnd.dece.data");
    map.put("uvvd", "application/vnd.dece.data");
    map.put("uvt", "application/vnd.dece.ttml+xml");
    map.put("uvvt", "application/vnd.dece.ttml+xml");
    map.put("uvx", "application/vnd.dece.unspecified");
    map.put("uvvx", "application/vnd.dece.unspecified");
    map.put("uvz", "application/vnd.dece.zip");
    map.put("uvvz", "application/vnd.dece.zip");
    map.put("fe_launch", "application/vnd.denovo.fcselayout-link");
    map.put("dna", "application/vnd.dna");
    map.put("mlp", "application/vnd.dolby.mlp");
    map.put("dpg", "application/vnd.dpgraph");
    map.put("dfac", "application/vnd.dreamfactory");
    map.put("kpxx", "application/vnd.ds-keypoint");
    map.put("ait", "application/vnd.dvb.ait");
    map.put("svc", "application/vnd.dvb.service");
    map.put("geo", "application/vnd.dynageo");
    map.put("mag", "application/vnd.ecowin.chart");
    map.put("nml", "application/vnd.enliven");
    map.put("esf", "application/vnd.epson.esf");
    map.put("msf", "application/vnd.epson.msf");
    map.put("qam", "application/vnd.epson.quickanime");
    map.put("slt", "application/vnd.epson.salt");
    map.put("ssf", "application/vnd.epson.ssf");
    map.put("es3", "application/vnd.eszigno3+xml");
    map.put("et3", "application/vnd.eszigno3+xml");
    map.put("ez2", "application/vnd.ezpix-album");
    map.put("ez3", "application/vnd.ezpix-package");
    map.put("fdf", "application/vnd.fdf");
    map.put("mseed", "application/vnd.fdsn.mseed");
    map.put("seed", "application/vnd.fdsn.seed");
    map.put("dataless", "application/vnd.fdsn.seed");
    map.put("gph", "application/vnd.flographit");
    map.put("ftc", "application/vnd.fluxtime.clip");
    map.put("fm", "application/vnd.framemaker");
    map.put("frame", "application/vnd.framemaker");
    map.put("maker", "application/vnd.framemaker");
    map.put("book", "application/vnd.framemaker");
    map.put("fnc", "application/vnd.frogans.fnc");
    map.put("ltf", "application/vnd.frogans.ltf");
    map.put("fsc", "application/vnd.fsc.weblaunch");
    map.put("oas", "application/vnd.fujitsu.oasys");
    map.put("oa2", "application/vnd.fujitsu.oasys2");
    map.put("oa3", "application/vnd.fujitsu.oasys3");
    map.put("fg5", "application/vnd.fujitsu.oasysgp");
    map.put("bh2", "application/vnd.fujitsu.oasysprs");
    map.put("ddd", "application/vnd.fujixerox.ddd");
    map.put("xdw", "application/vnd.fujixerox.docuworks");
    map.put("xbd", "application/vnd.fujixerox.docuworks.binder");
    map.put("fzs", "application/vnd.fuzzysheet");
    map.put("txd", "application/vnd.genomatix.tuxedo");
    map.put("ggb", "application/vnd.geogebra.file");
    map.put("ggs", "application/vnd.geogebra.slides");
    map.put("ggt", "application/vnd.geogebra.tool");
    map.put("gex", "application/vnd.geometry-explorer");
    map.put("gre", "application/vnd.geometry-explorer");
    map.put("gxt", "application/vnd.geonext");
    map.put("g2w", "application/vnd.geoplan");
    map.put("g3w", "application/vnd.geospace");
    map.put("gmx", "application/vnd.gmx");
    map.put("kml", "application/vnd.google-earth.kml+xml");
    map.put("kmz", "application/vnd.google-earth.kmz");
    map.put("gqf", "application/vnd.grafeq");
    map.put("gqs", "application/vnd.grafeq");
    map.put("gac", "application/vnd.groove-account");
    map.put("ghf", "application/vnd.groove-help");
    map.put("gim", "application/vnd.groove-identity-message");
    map.put("grv", "application/vnd.groove-injector");
    map.put("gtm", "application/vnd.groove-tool-message");
    map.put("tpl", "application/vnd.groove-tool-template");
    map.put("vcg", "application/vnd.groove-vcard");
    map.put("hal", "application/vnd.hal+xml");
    map.put("zmm", "application/vnd.handheld-entertainment+xml");
    map.put("hbci", "application/vnd.hbci");
    map.put("les", "application/vnd.hhe.lesson-player");
    map.put("hpgl", "application/vnd.hp-hpgl");
    map.put("hpid", "application/vnd.hp-hpid");
    map.put("hps", "application/vnd.hp-hps");
    map.put("jlt", "application/vnd.hp-jlyt");
    map.put("pcl", "application/vnd.hp-pcl");
    map.put("pclxl", "application/vnd.hp-pclxl");
    map.put("sfd-hdstx", "application/vnd.hydrostatix.sof-data");
    map.put("mpy", "application/vnd.ibm.minipay");
    map.put("afp", "application/vnd.ibm.modcap");
    map.put("listafp", "application/vnd.ibm.modcap");
    map.put("list3820", "application/vnd.ibm.modcap");
    map.put("irm", "application/vnd.ibm.rights-management");
    map.put("sc", "application/vnd.ibm.secure-container");
    map.put("icc", "application/vnd.iccprofile");
    map.put("icm", "application/vnd.iccprofile");
    map.put("igl", "application/vnd.igloader");
    map.put("ivp", "application/vnd.immervision-ivp");
    map.put("ivu", "application/vnd.immervision-ivu");
    map.put("igm", "application/vnd.insors.igm");
    map.put("xpw", "application/vnd.intercon.formnet");
    map.put("xpx", "application/vnd.intercon.formnet");
    map.put("i2g", "application/vnd.intergeo");
    map.put("qbo", "application/vnd.intu.qbo");
    map.put("qfx", "application/vnd.intu.qfx");
    map.put("rcprofile", "application/vnd.ipunplugged.rcprofile");
    map.put("irp", "application/vnd.irepository.package+xml");
    map.put("xpr", "application/vnd.is-xpr");
    map.put("fcs", "application/vnd.isac.fcs");
    map.put("jam", "application/vnd.jam");
    map.put("rms", "application/vnd.jcp.javame.midlet-rms");
    map.put("jisp", "application/vnd.jisp");
    map.put("joda", "application/vnd.joost.joda-archive");
    map.put("ktz", "application/vnd.kahootz");
    map.put("ktr", "application/vnd.kahootz");
    map.put("karbon", "application/vnd.kde.karbon");
    map.put("chrt", "application/vnd.kde.kchart");
    map.put("kfo", "application/vnd.kde.kformula");
    map.put("flw", "application/vnd.kde.kivio");
    map.put("kon", "application/vnd.kde.kontour");
    map.put("kpr", "application/vnd.kde.kpresenter");
    map.put("kpt", "application/vnd.kde.kpresenter");
    map.put("ksp", "application/vnd.kde.kspread");
    map.put("kwd", "application/vnd.kde.kword");
    map.put("kwt", "application/vnd.kde.kword");
    map.put("htke", "application/vnd.kenameaapp");
    map.put("kia", "application/vnd.kidspiration");
    map.put("kne", "application/vnd.kinar");
    map.put("knp", "application/vnd.kinar");
    map.put("skp", "application/vnd.koan");
    map.put("skd", "application/vnd.koan");
    map.put("skt", "application/vnd.koan");
    map.put("skm", "application/vnd.koan");
    map.put("sse", "application/vnd.kodak-descriptor");
    map.put("lasxml", "application/vnd.las.las+xml");
    map.put("lbd", "application/vnd.llamagraphics.life-balance.desktop");
    map.put("lbe", "application/vnd.llamagraphics.life-balance.exchange+xml");
    map.put("123", "application/vnd.lotus-1-2-3");
    map.put("apr", "application/vnd.lotus-approach");
    map.put("pre", "application/vnd.lotus-freelance");
    map.put("nsf", "application/vnd.lotus-notes");
    map.put("org", "application/vnd.lotus-organizer");
    map.put("scm", "application/vnd.lotus-screencam");
    map.put("lwp", "application/vnd.lotus-wordpro");
    map.put("portpkg", "application/vnd.macports.portpkg");
    map.put("mcd", "application/vnd.mcd");
    map.put("mc1", "application/vnd.medcalcdata");
    map.put("cdkey", "application/vnd.mediastation.cdkey");
    map.put("mwf", "application/vnd.mfer");
    map.put("mfm", "application/vnd.mfmp");
    map.put("flo", "application/vnd.micrografx.flo");
    map.put("igx", "application/vnd.micrografx.igx");
    map.put("mif", "application/vnd.mif");
    map.put("daf", "application/vnd.mobius.daf");
    map.put("dis", "application/vnd.mobius.dis");
    map.put("mbk", "application/vnd.mobius.mbk");
    map.put("mqy", "application/vnd.mobius.mqy");
    map.put("msl", "application/vnd.mobius.msl");
    map.put("plc", "application/vnd.mobius.plc");
    map.put("txf", "application/vnd.mobius.txf");
    map.put("mpn", "application/vnd.mophun.application");
    map.put("mpc", "application/vnd.mophun.certificate");
    map.put("xul", "application/vnd.mozilla.xul+xml");
    map.put("cil", "application/vnd.ms-artgalry");
    map.put("cab", "application/vnd.ms-cab-compressed");
    map.put("xls", "application/vnd.ms-excel");
    map.put("xlm", "application/vnd.ms-excel");
    map.put("xla", "application/vnd.ms-excel");
    map.put("xlc", "application/vnd.ms-excel");
    map.put("xlt", "application/vnd.ms-excel");
    map.put("xlw", "application/vnd.ms-excel");
    map.put("xlam", "application/vnd.ms-excel.addin.macroenabled.12");
    map.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroenabled.12");
    map.put("xlsm", "application/vnd.ms-excel.sheet.macroenabled.12");
    map.put("xltm", "application/vnd.ms-excel.template.macroenabled.12");
    map.put("eot", "application/vnd.ms-fontobject");
    map.put("chm", "application/vnd.ms-htmlhelp");
    map.put("ims", "application/vnd.ms-ims");
    map.put("lrm", "application/vnd.ms-lrm");
    map.put("thmx", "application/vnd.ms-officetheme");
    map.put("cat", "application/vnd.ms-pki.seccat");
    map.put("stl", "application/vnd.ms-pki.stl");
    map.put("ppt", "application/vnd.ms-powerpoint");
    map.put("pps", "application/vnd.ms-powerpoint");
    map.put("pot", "application/vnd.ms-powerpoint");
    map.put("ppam", "application/vnd.ms-powerpoint.addin.macroenabled.12");
    map.put("pptm", "application/vnd.ms-powerpoint.presentation.macroenabled.12");
    map.put("sldm", "application/vnd.ms-powerpoint.slide.macroenabled.12");
    map.put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroenabled.12");
    map.put("potm", "application/vnd.ms-powerpoint.template.macroenabled.12");
    map.put("mpp", "application/vnd.ms-project");
    map.put("mpt", "application/vnd.ms-project");
    map.put("docm", "application/vnd.ms-word.document.macroenabled.12");
    map.put("dotm", "application/vnd.ms-word.template.macroenabled.12");
    map.put("wps", "application/vnd.ms-works");
    map.put("wks", "application/vnd.ms-works");
    map.put("wcm", "application/vnd.ms-works");
    map.put("wdb", "application/vnd.ms-works");
    map.put("wpl", "application/vnd.ms-wpl");
    map.put("xps", "application/vnd.ms-xpsdocument");
    map.put("mseq", "application/vnd.mseq");
    map.put("mus", "application/vnd.musician");
    map.put("msty", "application/vnd.muvee.style");
    map.put("taglet", "application/vnd.mynfc");
    map.put("nlu", "application/vnd.neurolanguage.nlu");
    map.put("ntf", "application/vnd.nitf");
    map.put("nitf", "application/vnd.nitf");
    map.put("nnd", "application/vnd.noblenet-directory");
    map.put("nns", "application/vnd.noblenet-sealer");
    map.put("nnw", "application/vnd.noblenet-web");
    map.put("ngdat", "application/vnd.nokia.n-gage.data");
    map.put("n-gage", "application/vnd.nokia.n-gage.symbian.install");
    map.put("rpst", "application/vnd.nokia.radio-preset");
    map.put("rpss", "application/vnd.nokia.radio-presets");
    map.put("edm", "application/vnd.novadigm.edm");
    map.put("edx", "application/vnd.novadigm.edx");
    map.put("ext", "application/vnd.novadigm.ext");
    map.put("odc", "application/vnd.oasis.opendocument.chart");
    map.put("otc", "application/vnd.oasis.opendocument.chart-template");
    map.put("odb", "application/vnd.oasis.opendocument.database");
    map.put("odf", "application/vnd.oasis.opendocument.formula");
    map.put("odft", "application/vnd.oasis.opendocument.formula-template");
    map.put("odg", "application/vnd.oasis.opendocument.graphics");
    map.put("otg", "application/vnd.oasis.opendocument.graphics-template");
    map.put("odi", "application/vnd.oasis.opendocument.image");
    map.put("oti", "application/vnd.oasis.opendocument.image-template");
    map.put("odp", "application/vnd.oasis.opendocument.presentation");
    map.put("otp", "application/vnd.oasis.opendocument.presentation-template");
    map.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
    map.put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
    map.put("odt", "application/vnd.oasis.opendocument.text");
    map.put("odm", "application/vnd.oasis.opendocument.text-master");
    map.put("ott", "application/vnd.oasis.opendocument.text-template");
    map.put("oth", "application/vnd.oasis.opendocument.text-web");
    map.put("xo", "application/vnd.olpc-sugar");
    map.put("dd2", "application/vnd.oma.dd2+xml");
    map.put("oxt", "application/vnd.openofficeorg.extension");
    map.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    map.put("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide");
    map.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
    map.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
    map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    map.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
    map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    map.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
    map.put("mgp", "application/vnd.osgeo.mapguide.package");
    map.put("dp", "application/vnd.osgi.dp");
    map.put("esa", "application/vnd.osgi.subsystem");
    map.put("pdb", "application/vnd.palm");
    map.put("pqa", "application/vnd.palm");
    map.put("oprc", "application/vnd.palm");
    map.put("paw", "application/vnd.pawaafile");
    map.put("str", "application/vnd.pg.format");
    map.put("ei6", "application/vnd.pg.osasli");
    map.put("efif", "application/vnd.picsel");
    map.put("wg", "application/vnd.pmi.widget");
    map.put("plf", "application/vnd.pocketlearn");
    map.put("pbd", "application/vnd.powerbuilder6");
    map.put("box", "application/vnd.previewsystems.box");
    map.put("mgz", "application/vnd.proteus.magazine");
    map.put("qps", "application/vnd.publishare-delta-tree");
    map.put("ptid", "application/vnd.pvi.ptid1");
    map.put("qxd", "application/vnd.quark.quarkxpress");
    map.put("qxt", "application/vnd.quark.quarkxpress");
    map.put("qwd", "application/vnd.quark.quarkxpress");
    map.put("qwt", "application/vnd.quark.quarkxpress");
    map.put("qxl", "application/vnd.quark.quarkxpress");
    map.put("qxb", "application/vnd.quark.quarkxpress");
    map.put("bed", "application/vnd.realvnc.bed");
    map.put("mxl", "application/vnd.recordare.musicxml");
    map.put("musicxml", "application/vnd.recordare.musicxml+xml");
    map.put("cryptonote", "application/vnd.rig.cryptonote");
    map.put("cod", "application/vnd.rim.cod");
    map.put("rm", "application/vnd.rn-realmedia");
    map.put("rmvb", "application/vnd.rn-realmedia-vbr");
    map.put("link66", "application/vnd.route66.link66+xml");
    map.put("st", "application/vnd.sailingtracker.track");
    map.put("see", "application/vnd.seemail");
    map.put("sema", "application/vnd.sema");
    map.put("semd", "application/vnd.semd");
    map.put("semf", "application/vnd.semf");
    map.put("ifm", "application/vnd.shana.informed.formdata");
    map.put("itp", "application/vnd.shana.informed.formtemplate");
    map.put("iif", "application/vnd.shana.informed.interchange");
    map.put("ipk", "application/vnd.shana.informed.package");
    map.put("twd", "application/vnd.simtech-mindmapper");
    map.put("twds", "application/vnd.simtech-mindmapper");
    map.put("mmf", "application/vnd.smaf");
    map.put("teacher", "application/vnd.smart.teacher");
    map.put("sdkm", "application/vnd.solent.sdkm+xml");
    map.put("sdkd", "application/vnd.solent.sdkm+xml");
    map.put("dxp", "application/vnd.spotfire.dxp");
    map.put("sfs", "application/vnd.spotfire.sfs");
    map.put("sdc", "application/vnd.stardivision.calc");
    map.put("sda", "application/vnd.stardivision.draw");
    map.put("sdd", "application/vnd.stardivision.impress");
    map.put("smf", "application/vnd.stardivision.math");
    map.put("sdw", "application/vnd.stardivision.writer");
    map.put("vor", "application/vnd.stardivision.writer");
    map.put("sgl", "application/vnd.stardivision.writer-global");
    map.put("smzip", "application/vnd.stepmania.package");
    map.put("sm", "application/vnd.stepmania.stepchart");
    map.put("sxc", "application/vnd.sun.xml.calc");
    map.put("stc", "application/vnd.sun.xml.calc.template");
    map.put("sxd", "application/vnd.sun.xml.draw");
    map.put("std", "application/vnd.sun.xml.draw.template");
    map.put("sxi", "application/vnd.sun.xml.impress");
    map.put("sti", "application/vnd.sun.xml.impress.template");
    map.put("sxm", "application/vnd.sun.xml.math");
    map.put("sxw", "application/vnd.sun.xml.writer");
    map.put("sxg", "application/vnd.sun.xml.writer.global");
    map.put("stw", "application/vnd.sun.xml.writer.template");
    map.put("sus", "application/vnd.sus-calendar");
    map.put("susp", "application/vnd.sus-calendar");
    map.put("svd", "application/vnd.svd");
    map.put("sis", "application/vnd.symbian.install");
    map.put("sisx", "application/vnd.symbian.install");
    map.put("xsm", "application/vnd.syncml+xml");
    map.put("bdm", "application/vnd.syncml.dm+wbxml");
    map.put("xdm", "application/vnd.syncml.dm+xml");
    map.put("tao", "application/vnd.tao.intent-module-archive");
    map.put("pcap", "application/vnd.tcpdump.pcap");
    map.put("cap", "application/vnd.tcpdump.pcap");
    map.put("dmp", "application/vnd.tcpdump.pcap");
    map.put("tmo", "application/vnd.tmobile-livetv");
    map.put("tpt", "application/vnd.trid.tpt");
    map.put("mxs", "application/vnd.triscape.mxs");
    map.put("tra", "application/vnd.trueapp");
    map.put("ufd", "application/vnd.ufdl");
    map.put("ufdl", "application/vnd.ufdl");
    map.put("utz", "application/vnd.uiq.theme");
    map.put("umj", "application/vnd.umajin");
    map.put("unityweb", "application/vnd.unity");
    map.put("uoml", "application/vnd.uoml+xml");
    map.put("vcx", "application/vnd.vcx");
    map.put("vsd", "application/vnd.visio");
    map.put("vst", "application/vnd.visio");
    map.put("vss", "application/vnd.visio");
    map.put("vsw", "application/vnd.visio");
    map.put("vis", "application/vnd.visionary");
    map.put("vsf", "application/vnd.vsf");
    map.put("wbxml", "application/vnd.wap.wbxml");
    map.put("wmlc", "application/vnd.wap.wmlc");
    map.put("wmlsc", "application/vnd.wap.wmlscriptc");
    map.put("wtb", "application/vnd.webturbo");
    map.put("nbp", "application/vnd.wolfram.player");
    map.put("wpd", "application/vnd.wordperfect");
    map.put("wqd", "application/vnd.wqd");
    map.put("stf", "application/vnd.wt.stf");
    map.put("xar", "application/vnd.xara");
    map.put("xfdl", "application/vnd.xfdl");
    map.put("hvd", "application/vnd.yamaha.hv-dic");
    map.put("hvs", "application/vnd.yamaha.hv-script");
    map.put("hvp", "application/vnd.yamaha.hv-voice");
    map.put("osf", "application/vnd.yamaha.openscoreformat");
    map.put("osfpvg", "application/vnd.yamaha.openscoreformat.osfpvg+xml");
    map.put("saf", "application/vnd.yamaha.smaf-audio");
    map.put("spf", "application/vnd.yamaha.smaf-phrase");
    map.put("cmp", "application/vnd.yellowriver-custom-menu");
    map.put("zir", "application/vnd.zul");
    map.put("zirz", "application/vnd.zul");
    map.put("zaz", "application/vnd.zzazz.deck+xml");
    map.put("vxml", "application/voicexml+xml");
    map.put("wasm", "application/wasm");
    map.put("wgt", "application/widget");
    map.put("hlp", "application/winhlp");
    map.put("wsdl", "application/wsdl+xml");
    map.put("wspolicy", "application/wspolicy+xml");
    map.put("7z", "application/x-7z-compressed");
    map.put("abw", "application/x-abiword");
    map.put("ace", "application/x-ace-compressed");
    map.put("dmg", "application/x-apple-diskimage");
    map.put("aab", "application/x-authorware-bin");
    map.put("x32", "application/x-authorware-bin");
    map.put("u32", "application/x-authorware-bin");
    map.put("vox", "application/x-authorware-bin");
    map.put("aam", "application/x-authorware-map");
    map.put("aas", "application/x-authorware-seg");
    map.put("bcpio", "application/x-bcpio");
    map.put("torrent", "application/x-bittorrent");
    map.put("blb", "application/x-blorb");
    map.put("blorb", "application/x-blorb");
    map.put("bz", "application/x-bzip");
    map.put("bz2", "application/x-bzip2");
    map.put("boz", "application/x-bzip2");
    map.put("cbr", "application/x-cbr");
    map.put("cba", "application/x-cbr");
    map.put("cbt", "application/x-cbr");
    map.put("cbz", "application/x-cbr");
    map.put("cb7", "application/x-cbr");
    map.put("vcd", "application/x-cdlink");
    map.put("cfs", "application/x-cfs-compressed");
    map.put("chat", "application/x-chat");
    map.put("pgn", "application/x-chess-pgn");
    map.put("nsc", "application/x-conference");
    map.put("cpio", "application/x-cpio");
    map.put("csh", "application/x-csh");
    map.put("deb", "application/x-debian-package");
    map.put("udeb", "application/x-debian-package");
    map.put("dgc", "application/x-dgc-compressed");
    map.put("dir", "application/x-director");
    map.put("dcr", "application/x-director");
    map.put("dxr", "application/x-director");
    map.put("cst", "application/x-director");
    map.put("cct", "application/x-director");
    map.put("cxt", "application/x-director");
    map.put("w3d", "application/x-director");
    map.put("fgd", "application/x-director");
    map.put("swa", "application/x-director");
    map.put("wad", "application/x-doom");
    map.put("ncx", "application/x-dtbncx+xml");
    map.put("dtb", "application/x-dtbook+xml");
    map.put("res", "application/x-dtbresource+xml");
    map.put("dvi", "application/x-dvi");
    map.put("evy", "application/x-envoy");
    map.put("eva", "application/x-eva");
    map.put("bdf", "application/x-font-bdf");
    map.put("gsf", "application/x-font-ghostscript");
    map.put("psf", "application/x-font-linux-psf");
    map.put("pcf", "application/x-font-pcf");
    map.put("snf", "application/x-font-snf");
    map.put("pfa", "application/x-font-type1");
    map.put("pfb", "application/x-font-type1");
    map.put("pfm", "application/x-font-type1");
    map.put("afm", "application/x-font-type1");
    map.put("arc", "application/x-freearc");
    map.put("spl", "application/x-futuresplash");
    map.put("gca", "application/x-gca-compressed");
    map.put("ulx", "application/x-glulx");
    map.put("gnumeric", "application/x-gnumeric");
    map.put("gramps", "application/x-gramps-xml");
    map.put("gtar", "application/x-gtar");
    map.put("hdf", "application/x-hdf");
    map.put("install", "application/x-install-instructions");
    map.put("iso", "application/x-iso9660-image");
    map.put("jnlp", "application/x-java-jnlp-file");
    map.put("latex", "application/x-latex");
    map.put("lzh", "application/x-lzh-compressed");
    map.put("lha", "application/x-lzh-compressed");
    map.put("mie", "application/x-mie");
    map.put("prc", "application/x-mobipocket-ebook");
    map.put("mobi", "application/x-mobipocket-ebook");
    map.put("application", "application/x-ms-application");
    map.put("lnk", "application/x-ms-shortcut");
    map.put("wmd", "application/x-ms-wmd");
    map.put("wmz", "application/x-ms-wmz");
    map.put("xbap", "application/x-ms-xbap");
    map.put("mdb", "application/x-msaccess");
    map.put("obd", "application/x-msbinder");
    map.put("crd", "application/x-mscardfile");
    map.put("clp", "application/x-msclip");
    map.put("exe", "application/x-msdownload");
    map.put("dll", "application/x-msdownload");
    map.put("com", "application/x-msdownload");
    map.put("bat", "application/x-msdownload");
    map.put("msi", "application/x-msdownload");
    map.put("mvb", "application/x-msmediaview");
    map.put("m13", "application/x-msmediaview");
    map.put("m14", "application/x-msmediaview");
    map.put("wmf", "application/x-msmetafile");
    map.put("emf", "application/x-msmetafile");
    map.put("emz", "application/x-msmetafile");
    map.put("mny", "application/x-msmoney");
    map.put("pub", "application/x-mspublisher");
    map.put("scd", "application/x-msschedule");
    map.put("trm", "application/x-msterminal");
    map.put("wri", "application/x-mswrite");
    map.put("nc", "application/x-netcdf");
    map.put("cdf", "application/x-netcdf");
    map.put("nzb", "application/x-nzb");
    map.put("p12", "application/x-pkcs12");
    map.put("pfx", "application/x-pkcs12");
    map.put("p7b", "application/x-pkcs7-certificates");
    map.put("spc", "application/x-pkcs7-certificates");
    map.put("p7r", "application/x-pkcs7-certreqresp");
    map.put("rar", "application/x-rar-compressed");
    map.put("ris", "application/x-research-info-systems");
    map.put("sh", "application/x-sh");
    map.put("shar", "application/x-shar");
    map.put("swf", "application/x-shockwave-flash");
    map.put("xap", "application/x-silverlight-app");
    map.put("sql", "application/x-sql");
    map.put("sit", "application/x-stuffit");
    map.put("sitx", "application/x-stuffitx");
    map.put("srt", "application/x-subrip");
    map.put("sv4cpio", "application/x-sv4cpio");
    map.put("sv4crc", "application/x-sv4crc");
    map.put("t3", "application/x-t3vm-image");
    map.put("gam", "application/x-tads");
    map.put("tar", "application/x-tar");
    map.put("gz", "application/gzip");
    map.put("tcl", "application/x-tcl");
    map.put("tex", "application/x-tex");
    map.put("tfm", "application/x-tex-tfm");
    map.put("texinfo", "application/x-texinfo");
    map.put("texi", "application/x-texinfo");
    map.put("obj", "application/x-tgif");
    map.put("ustar", "application/x-ustar");
    map.put("src", "application/x-wais-source");
    map.put("der", "application/x-x509-ca-cert");
    map.put("crt", "application/x-x509-ca-cert");
    map.put("fig", "application/x-xfig");
    map.put("xlf", "application/x-xliff+xml");
    map.put("xpi", "application/x-xpinstall");
    map.put("xz", "application/x-xz");
    map.put("z1", "application/x-zmachine");
    map.put("z2", "application/x-zmachine");
    map.put("z3", "application/x-zmachine");
    map.put("z4", "application/x-zmachine");
    map.put("z5", "application/x-zmachine");
    map.put("z6", "application/x-zmachine");
    map.put("z7", "application/x-zmachine");
    map.put("z8", "application/x-zmachine");
    map.put("xaml", "application/xaml+xml");
    map.put("xdf", "application/xcap-diff+xml");
    map.put("xenc", "application/xenc+xml");
    map.put("xhtml", "application/xhtml+xml");
    map.put("xht", "application/xhtml+xml");
    map.put("xml", "application/xml");
    map.put("xsl", "application/xml");
    map.put("dtd", "application/xml-dtd");
    map.put("xop", "application/xop+xml");
    map.put("xpl", "application/xproc+xml");
    map.put("xslt", "application/xslt+xml");
    map.put("xspf", "application/xspf+xml");
    map.put("mxml", "application/xv+xml");
    map.put("xhvml", "application/xv+xml");
    map.put("xvml", "application/xv+xml");
    map.put("xvm", "application/xv+xml");
    map.put("yang", "application/yang");
    map.put("yin", "application/yin+xml");
    map.put("zip", "application/zip");
    map.put("adp", "audio/adpcm");
    map.put("au", "audio/basic");
    map.put("snd", "audio/basic");
    map.put("mid", "audio/midi");
    map.put("midi", "audio/midi");
    map.put("kar", "audio/midi");
    map.put("rmi", "audio/midi");
    map.put("m4a", "audio/mp4");
    map.put("mp4a", "audio/mp4");
    map.put("mpga", "audio/mpeg");
    map.put("mp2", "audio/mpeg");
    map.put("mp2a", "audio/mpeg");
    map.put("mp3", "audio/mpeg");
    map.put("m2a", "audio/mpeg");
    map.put("m3a", "audio/mpeg");
    map.put("oga", "audio/ogg");
    map.put("ogg", "audio/ogg");
    map.put("spx", "audio/ogg");
    map.put("opus", "audio/ogg");
    map.put("s3m", "audio/s3m");
    map.put("sil", "audio/silk");
    map.put("uva", "audio/vnd.dece.audio");
    map.put("uvva", "audio/vnd.dece.audio");
    map.put("eol", "audio/vnd.digital-winds");
    map.put("dra", "audio/vnd.dra");
    map.put("dts", "audio/vnd.dts");
    map.put("dtshd", "audio/vnd.dts.hd");
    map.put("lvp", "audio/vnd.lucent.voice");
    map.put("pya", "audio/vnd.ms-playready.media.pya");
    map.put("ecelp4800", "audio/vnd.nuera.ecelp4800");
    map.put("ecelp7470", "audio/vnd.nuera.ecelp7470");
    map.put("ecelp9600", "audio/vnd.nuera.ecelp9600");
    map.put("rip", "audio/vnd.rip");
    map.put("weba", "audio/webm");
    map.put("aac", "audio/x-aac");
    map.put("aif", "audio/x-aiff");
    map.put("aiff", "audio/x-aiff");
    map.put("aifc", "audio/x-aiff");
    map.put("caf", "audio/x-caf");
    map.put("flac", "audio/x-flac");
    map.put("mka", "audio/x-matroska");
    map.put("m3u", "audio/x-mpegurl");
    map.put("wax", "audio/x-ms-wax");
    map.put("wma", "audio/x-ms-wma");
    map.put("ram", "audio/x-pn-realaudio");
    map.put("ra", "audio/x-pn-realaudio");
    map.put("rmp", "audio/x-pn-realaudio-plugin");
    map.put("wav", "audio/x-wav");
    map.put("xm", "audio/xm");
    map.put("cdx", "chemical/x-cdx");
    map.put("cif", "chemical/x-cif");
    map.put("cmdf", "chemical/x-cmdf");
    map.put("cml", "chemical/x-cml");
    map.put("csml", "chemical/x-csml");
    map.put("xyz", "chemical/x-xyz");
    map.put("ttc", "font/collection");
    map.put("otf", "font/otf");
    map.put("ttf", "font/ttf");
    map.put("woff", "font/woff");
    map.put("woff2", "font/woff2");
    map.put("avif", "image/avif");
    map.put("bmp", "image/bmp");
    map.put("cgm", "image/cgm");
    map.put("g3", "image/g3fax");
    map.put("gif", "image/gif");
    map.put("ief", "image/ief");
    map.put("jpeg", "image/jpeg");
    map.put("jpg", "image/jpeg");
    map.put("jpe", "image/jpeg");
    map.put("jxl", "image/jxl");
    map.put("ktx", "image/ktx");
    map.put("png", "image/png");
    map.put("btif", "image/prs.btif");
    map.put("sgi", "image/sgi");
    map.put("svg", "image/svg+xml");
    map.put("svgz", "image/svg+xml");
    map.put("tiff", "image/tiff");
    map.put("tif", "image/tiff");
    map.put("psd", "image/vnd.adobe.photoshop");
    map.put("uvi", "image/vnd.dece.graphic");
    map.put("uvvi", "image/vnd.dece.graphic");
    map.put("uvg", "image/vnd.dece.graphic");
    map.put("uvvg", "image/vnd.dece.graphic");
    map.put("djvu", "image/vnd.djvu");
    map.put("djv", "image/vnd.djvu");
    map.put("sub", "image/vnd.dvb.subtitle");
    map.put("dwg", "image/vnd.dwg");
    map.put("dxf", "image/vnd.dxf");
    map.put("fbs", "image/vnd.fastbidsheet");
    map.put("fpx", "image/vnd.fpx");
    map.put("fst", "image/vnd.fst");
    map.put("mmr", "image/vnd.fujixerox.edmics-mmr");
    map.put("rlc", "image/vnd.fujixerox.edmics-rlc");
    map.put("mdi", "image/vnd.ms-modi");
    map.put("wdp", "image/vnd.ms-photo");
    map.put("npx", "image/vnd.net-fpx");
    map.put("wbmp", "image/vnd.wap.wbmp");
    map.put("xif", "image/vnd.xiff");
    map.put("webp", "image/webp");
    map.put("3ds", "image/x-3ds");
    map.put("ras", "image/x-cmu-raster");
    map.put("cmx", "image/x-cmx");
    map.put("fh", "image/x-freehand");
    map.put("fhc", "image/x-freehand");
    map.put("fh4", "image/x-freehand");
    map.put("fh5", "image/x-freehand");
    map.put("fh7", "image/x-freehand");
    map.put("ico", "image/x-icon");
    map.put("sid", "image/x-mrsid-image");
    map.put("pcx", "image/x-pcx");
    map.put("pic", "image/x-pict");
    map.put("pct", "image/x-pict");
    map.put("pnm", "image/x-portable-anymap");
    map.put("pbm", "image/x-portable-bitmap");
    map.put("pgm", "image/x-portable-graymap");
    map.put("ppm", "image/x-portable-pixmap");
    map.put("rgb", "image/x-rgb");
    map.put("tga", "image/x-tga");
    map.put("xbm", "image/x-xbitmap");
    map.put("xpm", "image/x-xpixmap");
    map.put("xwd", "image/x-xwindowdump");
    map.put("eml", "message/rfc822");
    map.put("mime", "message/rfc822");
    map.put("igs", "model/iges");
    map.put("iges", "model/iges");
    map.put("msh", "model/mesh");
    map.put("mesh", "model/mesh");
    map.put("silo", "model/mesh");
    map.put("dae", "model/vnd.collada+xml");
    map.put("dwf", "model/vnd.dwf");
    map.put("gdl", "model/vnd.gdl");
    map.put("gtw", "model/vnd.gtw");
    map.put("vtu", "model/vnd.vtu");
    map.put("wrl", "model/vrml");
    map.put("vrml", "model/vrml");
    map.put("x3db", "model/x3d+binary");
    map.put("x3dbz", "model/x3d+binary");
    map.put("x3dv", "model/x3d+vrml");
    map.put("x3dvz", "model/x3d+vrml");
    map.put("x3d", "model/x3d+xml");
    map.put("x3dz", "model/x3d+xml");
    map.put("appcache", "text/cache-manifest");
    map.put("ics", "text/calendar");
    map.put("ifb", "text/calendar");
    map.put("css", "text/css");
    map.put("csv", "text/csv");
    map.put("html", "text/html");
    map.put("htm", "text/html");
    map.put("js", "text/javascript");
    map.put("mjs", "text/javascript");
    map.put("n3", "text/n3");
    map.put("txt", "text/plain");
    map.put("text", "text/plain");
    map.put("conf", "text/plain");
    map.put("def", "text/plain");
    map.put("list", "text/plain");
    map.put("log", "text/plain");
    map.put("in", "text/plain");
    map.put("dsc", "text/prs.lines.tag");
    map.put("rtx", "text/richtext");
    map.put("sgml", "text/sgml");
    map.put("sgm", "text/sgml");
    map.put("tsv", "text/tab-separated-values");
    map.put("t", "text/troff");
    map.put("tr", "text/troff");
    map.put("roff", "text/troff");
    map.put("man", "text/troff");
    map.put("me", "text/troff");
    map.put("ms", "text/troff");
    map.put("ttl", "text/turtle");
    map.put("uri", "text/uri-list");
    map.put("uris", "text/uri-list");
    map.put("urls", "text/uri-list");
    map.put("vcard", "text/vcard");
    map.put("curl", "text/vnd.curl");
    map.put("dcurl", "text/vnd.curl.dcurl");
    map.put("mcurl", "text/vnd.curl.mcurl");
    map.put("scurl", "text/vnd.curl.scurl");
    map.put("fly", "text/vnd.fly");
    map.put("flx", "text/vnd.fmi.flexstor");
    map.put("gv", "text/vnd.graphviz");
    map.put("3dml", "text/vnd.in3d.3dml");
    map.put("spot", "text/vnd.in3d.spot");
    map.put("jad", "text/vnd.sun.j2me.app-descriptor");
    map.put("wml", "text/vnd.wap.wml");
    map.put("wmls", "text/vnd.wap.wmlscript");
    map.put("s", "text/x-asm");
    map.put("asm", "text/x-asm");
    map.put("c", "text/x-c");
    map.put("cc", "text/x-c");
    map.put("cxx", "text/x-c");
    map.put("cpp", "text/x-c");
    map.put("h", "text/x-c");
    map.put("hh", "text/x-c");
    map.put("dic", "text/x-c");
    map.put("f", "text/x-fortran");
    map.put("for", "text/x-fortran");
    map.put("f77", "text/x-fortran");
    map.put("f90", "text/x-fortran");
    map.put("java", "text/x-java-source");
    map.put("nfo", "text/x-nfo");
    map.put("opml", "text/x-opml");
    map.put("p", "text/x-pascal");
    map.put("pas", "text/x-pascal");
    map.put("etx", "text/x-setext");
    map.put("sfv", "text/x-sfv");
    map.put("uu", "text/x-uuencode");
    map.put("vcs", "text/x-vcalendar");
    map.put("vcf", "text/x-vcard");
    map.put("3gp", "video/3gpp");
    map.put("3g2", "video/3gpp2");
    map.put("h261", "video/h261");
    map.put("h263", "video/h263");
    map.put("h264", "video/h264");
    map.put("jpgv", "video/jpeg");
    map.put("jpm", "video/jpm");
    map.put("jpgm", "video/jpm");
    map.put("mj2", "video/mj2");
    map.put("mjp2", "video/mj2");
    map.put("ts", "video/mp2t");
    map.put("m2t", "video/mp2t");
    map.put("m2ts", "video/mp2t");
    map.put("mts", "video/mp2t");
    map.put("mp4", "video/mp4");
    map.put("mp4v", "video/mp4");
    map.put("mpg4", "video/mp4");
    map.put("mpeg", "video/mpeg");
    map.put("mpg", "video/mpeg");
    map.put("mpe", "video/mpeg");
    map.put("m1v", "video/mpeg");
    map.put("m2v", "video/mpeg");
    map.put("ogv", "video/ogg");
    map.put("qt", "video/quicktime");
    map.put("mov", "video/quicktime");
    map.put("uvh", "video/vnd.dece.hd");
    map.put("uvvh", "video/vnd.dece.hd");
    map.put("uvm", "video/vnd.dece.mobile");
    map.put("uvvm", "video/vnd.dece.mobile");
    map.put("uvp", "video/vnd.dece.pd");
    map.put("uvvp", "video/vnd.dece.pd");
    map.put("uvs", "video/vnd.dece.sd");
    map.put("uvvs", "video/vnd.dece.sd");
    map.put("uvv", "video/vnd.dece.video");
    map.put("uvvv", "video/vnd.dece.video");
    map.put("dvb", "video/vnd.dvb.file");
    map.put("fvt", "video/vnd.fvt");
    map.put("mxu", "video/vnd.mpegurl");
    map.put("m4u", "video/vnd.mpegurl");
    map.put("pyv", "video/vnd.ms-playready.media.pyv");
    map.put("uvu", "video/vnd.uvvu.mp4");
    map.put("uvvu", "video/vnd.uvvu.mp4");
    map.put("viv", "video/vnd.vivo");
    map.put("webm", "video/webm");
    map.put("f4v", "video/x-f4v");
    map.put("fli", "video/x-fli");
    map.put("flv", "video/x-flv");
    map.put("m4v", "video/x-m4v");
    map.put("mkv", "video/x-matroska");
    map.put("mk3d", "video/x-matroska");
    map.put("mks", "video/x-matroska");
    map.put("mng", "video/x-mng");
    map.put("asf", "video/x-ms-asf");
    map.put("asx", "video/x-ms-asf");
    map.put("vob", "video/x-ms-vob");
    map.put("wm", "video/x-ms-wm");
    map.put("wmv", "video/x-ms-wmv");
    map.put("wmx", "video/x-ms-wmx");
    map.put("wvx", "video/x-ms-wvx");
    map.put("avi", "video/x-msvideo");
    map.put("movie", "video/x-sgi-movie");
    map.put("smv", "video/x-smv");
    map.put("ice", "x-conference/x-cooltalk");

    this.mimeTypeMap = map;
    this.mimeTypes = Collections.newSetFromMap(new ConcurrentHashMap<>());
    this.mimeTypes.addAll(map.values());
  }

  /**
   * Retrieves the MIME type for a given file name based on its extension.
   *
   * @param fileName the name of the file to determine the MIME type for
   * @return the corresponding MIME type
   * @throws IllegalArgumentException if the file name is invalid or no MIME type is found and
   *     fallback is disabled
   */
  public String getMimeType(String fileName) {
    if (fileName == null || fileName.isBlank()) {
      throw new IllegalArgumentException(
          "File name must not be null, empty, or contain only whitespace.");
    }

    String extension = getFileExtension(fileName);
    if (extension.isEmpty()) {
      throw new IllegalArgumentException(
          "File extension could not be determined from '" + fileName + "'.");
    }

    synchronized (lock) {
      if (mimeTypeMap.containsKey(extension)) {
        return mimeTypeMap.get(extension);
      }

      if (allowFallbackMimeType) {
        return defaultMimeType;
      }

      throw new IllegalArgumentException(
          "MIME type could not be determined for '"
              + fileName
              + "'. Provide a MIME type explicitly or enable fallback.");
    }
  }

  /**
   * Checks if a given MIME type is valid according to the known set or configuration.
   *
   * @param mimeType the MIME type to validate
   * @return true if the MIME type is valid, false otherwise
   * @throws IllegalArgumentException if the MIME type is null, empty, or only whitespace
   */
  public boolean isValidMimeType(String mimeType) {
    if (mimeType == null || mimeType.isBlank()) {
      throw new IllegalArgumentException(
          "MIME type must not be null, empty, or contain only whitespace.");
    }

    synchronized (lock) {
      return mimeTypes.contains(mimeType) || allowUnknownMimeType;
    }
  }

  /**
   * Adds or updates a MIME type mapping for a given file extension.
   *
   * @param extension the file extension (e.g., "pdf")
   * @param mimeType the MIME type to associate with the extension (e.g., "application/pdf")
   * @return true if this is a new entry, false if it updates an existing one
   * @throws IllegalArgumentException if extension or MIME type is null, empty, or only whitespace
   */
  public boolean addOrUpdateMimeType(String extension, String mimeType) {
    if (extension == null || extension.isBlank()) {
      throw new IllegalArgumentException(
          "Extension must not be null, empty, or contain only whitespace.");
    }
    if (mimeType == null || mimeType.isBlank()) {
      throw new IllegalArgumentException(
          "MIME type must not be null, empty, or contain only whitespace.");
    }

    extension = extension.trim().toLowerCase();

    synchronized (lock) {
      boolean isNewEntry = !mimeTypeMap.containsKey(extension);

      if (mimeTypeMap.containsKey(extension)) {
        mimeTypes.remove(mimeTypeMap.get(extension));
      }

      mimeTypeMap.put(extension, mimeType);
      mimeTypes.add(mimeType);
      return isNewEntry;
    }
  }

  /**
   * Retrieves an unmodifiable copy of the current MIME type mappings.
   *
   * @return a map of file extensions to MIME types
   */
  public Map<String, String> getMimeTypeMappings() {
    synchronized (lock) {
      return Map.copyOf(mimeTypeMap);
    }
  }

  /**
   * Extracts the file extension from a file name.
   *
   * @param fileName the name of the file
   * @return the extension (lowercase), or an empty string if none is found
   */
  private String getFileExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    return (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1)
        ? ""
        : fileName.substring(lastDotIndex + 1).toLowerCase();
  }

  /**
   * Retrieves the default MIME type used as a fallback.
   *
   * @return the default MIME type
   */
  public String getDefaultMimeType() {
    return defaultMimeType;
  }

  /**
   * Sets the default MIME type to use as a fallback.
   *
   * @param defaultMimeType the MIME type to set as default
   */
  public void setDefaultMimeType(String defaultMimeType) {
    this.defaultMimeType = defaultMimeType;
  }

  /**
   * Checks if fallback to the default MIME type is allowed when no mapping is found.
   *
   * @return true if fallback is allowed, false otherwise
   */
  public boolean isAllowFallbackMimeType() {
    return allowFallbackMimeType;
  }

  /**
   * Sets whether to allow fallback to the default MIME type when no mapping is found.
   *
   * @param allowFallbackMimeType true to enable fallback, false to disable
   */
  public void setAllowFallbackMimeType(boolean allowFallbackMimeType) {
    this.allowFallbackMimeType = allowFallbackMimeType;
  }

  /**
   * Checks if unknown MIME types (not in the predefined set) are allowed.
   *
   * @return true if unknown MIME types are allowed, false otherwise
   */
  public boolean isAllowUnknownMimeType() {
    return allowUnknownMimeType;
  }

  /**
   * Sets whether to allow unknown MIME types not in the predefined set.
   *
   * @param allowUnknownMimeType true to allow unknown types, false to restrict to known types
   */
  public void setAllowUnknownMimeType(boolean allowUnknownMimeType) {
    this.allowUnknownMimeType = allowUnknownMimeType;
  }
}

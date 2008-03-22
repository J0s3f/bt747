#!/bin/sh

#################################################################
#
# ABOUT
# =====
#
# DownloadFonts.sh is a script to accompany the
# Unicode Font Guide For Free/Libre Open Source Operating Systems
# at http://unifont.org/fontguide/
#
# This script allows you to quickly and painlessly download a
# large selection of the best FLOSS international fonts listed
# on the Font Guide.
#
# COPYRIGHT
# =========
#
# Copyright (c) 2005, 2007 by Edward H. Trager
# Released under the GNU General Public License (GPL).
#
#
# CONTRIBUTORS
# ============
#
# I would like to thank the following contributors who have
# provided generous help in improving and updating this script:
#
#  * Dugan Chen http://www.vcn.bc.ca/~dugan/
#
# RELEASE HISTORY
# ===============
#
# * This version is dated 2007.10.08.
#
# * 2007.10.08  - Dugan Chen added SFHOST as an environment variable
#                 specifying which Sourceforge host you want to use.
#
# * 2007.04.20  - Significantly updated by Dugan Chen with additional
#                 minor updates by Ed Trager as follows:
#
#                   - A number of broken links have been fixed.
#                   - Added "-c" to "wget" command to allow pausing and
#                     resuming of the script.
#                   - All extraction is done after completion of
#                     downloading
#                   - All archive packages, text files, and any other
#                     non-font files are removed, leaving just the
#                     font files.  (If you really want to see the
#                     sample PDFs or license files accompanying a
#                     font, just download it individually instead of
#                     using this script).
#                   - Added Sanskritweb.org's Chandas and Uttara
#                     Devanagari fonts to the download script.
#
# * 2007.02.28  - Updated by Dugan Chen to work on modern *nix
#                 distributions like Ubuntu.
#
# * 2005.09.06  - First released by Ed Trager sometime in 2005.
#
#################################################################

#Uncomment the Sourceforge host that gives you the fastest
#downloads.

#SFHOST=jaist       # Ishikawa, Japan
#SFHOST=nchc        # Tainan, Taiwan
#SFHOST=switch      # Lausanne, Switzerland
#SFHOST=heanet      # Dublin, Ireland
#SFHOST=garr        # Bologna, Italy
SFHOST=surfnet     # Amsterdam, Netherlands
#SFHOST=kent        # Kent, UK
#SFHOST=easynews    # Phoenix, AZ
#SFHOST=internap    # San Jose, CA
#SFHOST=superb-east # McLean, Virginia
#SFHOST=superb-west # Seattle, Washington
#SFHOST=ufpr        # Curitiba, Brazil

SFDOMAIN=dl.sourceforge.net
SOURCEFORGE=$SFHOST.$SFDOMAIN

red() {
	local COLOR="\033[31m"
	local NOCOLOR="\033[0m"
	echo -e -n $COLOR
	echo -n $1
	echo -e $NOCOLOR
}

green() {
	local COLOR="\033[32m"
	local NOCOLOR="\033[0m"
	echo -e -n $COLOR
	echo -n $1
	echo -e $NOCOLOR
}

blue() {
	local COLOR="\033[34m"
	local NOCOLOR="\033[0m"
	echo -e -n $COLOR
	echo -n $1
	echo -e $NOCOLOR
}


blue "Checking for 'fonts' directory ..."
if test -d fonts
then	blue "... DIRECTORY EXISTS ..."
	cd fonts
else
	blue "... CREATING 'fonts' DIRECTORY ..."
	mkdir fonts && cd fonts
fi

blue "Starting ..."

green "=== PAN UNICODE SECTION ==="
green "=== PAN UNICODE SECTION ==="
green "=== PAN UNICODE SECTION ==="

#blue "Attempting to download Bitstream Cyberbit ..."
#wget -c ftp://ftp8.netscape.com/pub/communicator/extras/fonts/windows/Cyberbit.ZIP

blue "Attempting to download MPH Damase ..."
wget -c http://www.travelphrases.info/downloads/damase_v.2.zip

green "=== AFRICAN SCRIPTS SECTION ==="
green "=== AFRICAN SCRIPTS SECTION ==="
green "=== AFRICAN SCRIPTS SECTION ==="

blue "Attempting to download Amharic Jiret ..."
wget -c http://www.senamirmir.com/download/jiret.zip

blue "Attempting to download Amharic GF Zemen ..."
wget -c ftp://ftp.ethiopic.org/pub/fonts/TrueType/gfzemenu.ttf

blue "Attempting to download Tifinagh Hapax Berbere ..."
wget -c http://www.hapax.qc.ca/polices/hapaxber.ttf

green "=== AMERICAN SCRIPTS SECTION ==="
green "=== AMERICAN SCRIPTS SECTION ==="
green "=== AMERICAN SCRIPTS SECTION ==="

blue "Attempting to download Language Geek Aboriginal Serif ..."
wget -c http://www.languagegeek.com/font/abserif.zip

blue "Attempting to download Language Geek Aboriginal Sans ..."
wget -c http://www.languagegeek.com/font/absans.zip

green "=== EUROPEAN SCRIPTS SECTION ==="
green "=== EUROPEAN SCRIPTS SECTION ==="
green "=== EUROPEAN SCRIPTS SECTION ==="

blue "Attempting to download DejaVu LGC ..."
wget -c http://$SOURCEFORGE/sourceforge/dejavu/dejavu-lgc-ttf-2.16.tar.bz2

blue "Attempting to download MgOpen fonts from www.ellak.gr ..."
wget -c http://www.ellak.gr/fonts/mgopen/files/MgOpen.tar.gz

blue "Attempting to download Gentium font from SIL ..."
wget -c "http://scripts.sil.org/cms/scripts/render_download.php?site_id=nrsi&format=file&media_id=Gentium_101_LT&filename=fonts-ttf-gentium-1.0.1.tar.bz2" -O gentium-1.0.1.tar.bz2

blue "Attempting to download three Georgian Fonts from aiet.qartuli.net ..."
wget -c http://aiet.qartuli.net/download/BPG_Glaho.ttf.gz
wget -c http://aiet.qartuli.net/download/BPG_Chveulebrivi.ttf.gz
wget -c http://aiet.qartuli.net/download/BPG_Courier.ttf.gz

blue "Attempting to download Darren Rigby's Hindsight Unicode (Armenian et al.) ..."
wget -c http://dartcanada.tripod.com/Objets/Zips/Hindsite.zip

green "=== MIDDLE EASTERN SCRIPTS SECTION ==="
green "=== MIDDLE EASTERN SCRIPTS SECTION ==="
green "=== MIDDLE EASTERN SCRIPTS SECTION ==="

blue "Attempting to download Arabeyes vector fonts from Sourceforge mirror ..."
wget -c -t 3 "http://$SOURCEFORGE/sourceforge/arabeyes/ae_fonts1_ttf_1.1.tar.bz2" -O aefonts.tar.bz2

blue "Attempting to download Society of Biblical Literature Hebrew font ..."
wget -c http://www.sbl-site.org/Fonts/SBLHebrew-Distributionv107.zip

blue "Attempting to download Meltho Syriac fonts ..."
wget -c http://www.bethmardutho.org/files/melthofonts-1.21.tar.gz

blue "Attempting to download Dhivehi Politics' Dhivehi fonts ..."
wget -c http://www.dhipolitics.com/fonts/fonts.zip

green "=== SOUTH ASIAN SCRIPTS SECTION ==="
green "=== SOUTH ASIAN SCRIPTS SECTION ==="
green "=== SOUTH ASIAN SCRIPTS SECTION ==="

blue "Attempting to download Mihail Bayaryn's Chandas and Uttara Devanagari fonts for Sanskrit ..."
wget -c http://www.sanskritweb.org/cakram/chandas.ttf
wget -c http://www.sanskritweb.org/cakram/uttara.ttf

blue "Attempting to download Madan Puraskar Pustakalaya Devanagari fonts ..."
wget -c http://www.mpp.org.np/detail_guide/fonts/Kalimati.ttf
wget -c http://www.mpp.org.np/detail_guide/fonts/Kanjirowa.ttf
wget -c http://www.mpp.org.np/detail_guide/fonts/tr.ttf
wget -c http://www.mpp.org.np/detail_guide/fonts/Samanata.ttf

blue "Attempting to download five Devanagari Unicode OpenType fonts from TDIL ..."
wget -c http://tdil.mit.gov.in/download/Raghu.zip
wget -c http://tdil.mit.gov.in/download/GISTYogeshN.zip
wget -c http://tdil.mit.gov.in/download/surekh.zip
wget -c http://tdil.mit.gov.in/download/gargi.zip
wget -c http://tdil.mit.gov.in/download/janahindi.zip

blue "Attempting to download Jana Marathi font ..."
wget -c http://tdil.mit.gov.in/download/JanaMarathi.zip

blue "Attempting to download four ekushey.org Bengali fonts ..."
wget -c http://$SOURCEFORGE/sourceforge/ekushey/SolaimanLipi_04_07_05.ttf
wget -c http://$SOURCEFORGE/sourceforge/ekushey/Rupali_26_6_05.ttf
wget -c http://$SOURCEFORGE/sourceforge/ekushey/Sharifa_11-08-2005.ttf
wget -c http://$SOURCEFORGE/sourceforge/ekushey/Punarbhaba_11-08-2005.ttf

#blue "Attempting to download two Gujarati fonts from Utkarsh ..."
wget -c http://utkarsh.org/downloads/Rekha-medium.ttf
wget -c http://utkarsh.org/downloads/aakar-medium.ttf

blue "Attempting to download the Saab Gurmukhi (Punjabi) Unicode font ..."
wget -c http://$SOURCEFORGE/sourceforge/guca/saab.0.91.zip

blue "Attempting to download Sampige Kannada Unicode font ..."
wget -c http://brahmi.sourceforge.net/dl/Sampige.ttf

blue "Attempting to download Jana Kannada Unicode font ..."
wget -c http://tdil.mit.gov.in/download/JanaKannada.zip

blue "Attempting to download FSF India's Malayalam project font ..."
wget -c http://kelkiosk.keltron.co.in/malayalam/malayalam.ttf.gz

blue "Attempting to download Jana Malayalam font ..."
wget -c http://tdil.mit.gov.in/download/JanaMalayalam.zip

blue "Attempting to download Oriya font from sarovar.org ..."
wget -c http://oriya.sarovar.org/download/utkal.ttf.gz

blue "Attempting to download Sinhala Malithi Web font ..."
wget -c http://www.mrt.ac.lk/sinhala/FM-MalithiUW46.ttf

blue "Attempting to download Jana Tamil font ..."
wget -c http://tdil.mit.gov.in/download/JanaTamil.zip

blue "Attempting to download Telugu Pothana2000 font ..."
wget -c http://www.kavya-nandanam.com/Pothana2k.zip

blue "Attempting to download Tibetan Machine Uni font from THDL ..."
wget -c http://iris.lib.virginia.edu/tibet/tools/dls/fonts/TibetanMachineUnicodeFont.zip

green "=== SOUTH EAST ASIAN SCRIPTS SECTION ==="
green "=== SOUTH EAST ASIAN SCRIPTS SECTION ==="
green "=== SOUTH EAST ASIAN SCRIPTS SECTION ==="

blue "Attempting to download KhmerOS Khmer Unicode fonts ..."
wget -c http://$SOURCEFORGE/sourceforge/khmer/All_KhmerOS_31.zip

blue "Attempting to download Saysettha Lao Opentype font ..."
wget -c http://www.laoscript.net/downloads/saysettha_ot.zip

blue "Attempting to download Fixedsys.org's Myanmar Yangon Opentype font ..."
wget -c http://bonedaddy.net/pabs3/files/tmp/fonts/yngn.otf

blue "Attempting to download Paul Morrow's Philippine font ... "
wget -c http://www.mts.net/~pmorrow/tagdoc93.zip

blue "Attempting to download Valentium Tai Le font from www.wazu.jp ..."
wget -c http://www.wazu.jp/downloads/taileval.zip

blue "Attempting to download ThaiFonts-Scalable Unicode fonts ..."
wget -c ftp://linux.thai.net/pub/thailinux/software/thaifonts-scalable/thaifonts-scalable-0.4.3.1.tar.gz

green "=== SCIENTIFIC AND SCHOLARLY FONTS SECTION ==="
green "=== SCIENTIFIC AND SCHOLARLY FONTS SECTION ==="
green "=== SCIENTIFIC AND SCHOLARLY FONTS SECTION ==="

blue "Attempting to download David Perry's Cardo Unicode font for classicists ..."
wget -c http://www.scholarsfonts.net/cardo98.zip

green "=== EAST ASIAN SCRIPTS SECTION ==="
green "=== EAST ASIAN SCRIPTS SECTION ==="
green "=== EAST ASIAN SCRIPTS SECTION ==="

blue "Attempting to download Firefly's AR PL New Song font ..."
red  "WARNING: East Asian fonts are very large ... expect long download times!"
wget -c http://www.study-area.org/apt/firefly-font/fireflysung-1.3.0.tar.gz

blue "Attempting to download Debian Taiwan's Arphic ukai and uming fonts ..."
red  "WARNING: East Asian fonts are very large ... expect long download times!"
wget -c http://ftp.tw.debian.org/debian/pool/main/t/ttf-arphic-uming/ttf-arphic-uming_0.1.20060928.orig.tar.gz
wget -c http://ftp.tw.debian.org/debian/pool/main/t/ttf-arphic-ukai/ttf-arphic-ukai_0.1.20060928.orig.tar.gz

blue "Attempting to download Japanese Sazanami Mincho and Gothic fonts ..."
wget -c http://osdn.dl.sourceforge.jp/efont/10087/sazanami-20040629.tar.bz2 -O sazanami-20040629.tar.bz2

blue "Attempting to download SIL Yi Unicode font ..."
wget -c "http://scripts.sil.org/cms/scripts/render_download.php?site_id=nrsi&format=file&media_id=SILYI.ZIP&filename=SILYI.zip" -O silyi.zip

blue "Attempting to download Korean Un fonts package ..."
red "WARNING: This particular server does not support resuming!"
wget http://kldp.net/frs/download.php/1425/un-fonts-core-1.0.tar.gz -O un-fonts-core-1.0.tar.gz

# Font Extraction

green "=== EXTRACTION SECTION ==="
green "=== EXTRACTION SECTION ==="
green "=== EXTRACTION SECTION ==="

#blue "Extracting Bitstream Cyberbit"
#unzip Cyberbit.ZIP && rm Cyberbit.ZIP

blue "Extracting MPH Damase ..."
unzip damase_v.2.zip && rm damase_v.2.zip

blue "Extracting Amharic Jiret ..."
unzip -j jiret.zip && rm jiret.zip

blue "Extracing Language Geek Aboriginal Serif ..."
unzip abserif.zip && rm abserif.zip

blue "Extracting Language Geek Aboriginal Sans ..."
unzip absans.zip && rm absans.zip

blue "Extracting DejaVu LGC ..."
tar xvfj dejavu-lgc-ttf-2.16.tar.bz2 && rm dejavu-lgc-ttf-2.16.tar.bz2
mv dejavu-lgc-ttf-2.16/*.ttf . && rm -rf dejavu-lgc-ttf-2.16

blue "Extracing MgOpen fonts ..."
tar xzvf MgOpen.tar.gz && rm MgOpen.tar.gz

blue "Extracting Gentium font ..."
tar xjvf gentium-1.0.1.tar.bz2 && rm gentium-1.0.1.tar.bz2
mv fonts-ttf-gentium-1.0.1/*.ttf . && rm -rf fonts-ttf-gentium-1.0.1

blue "Extracting three Georgian fonts ..."
gzip -d BPG_Glaho.ttf.gz BPG_Chveulebrivi.ttf.gz BPG_Courier.ttf.gz

blue "Extracting Darren Rigby's Hindsight Unicode (Armenian et al.) ..."
unzip Hindsite.zip && rm Hindsite.zip
rm legal_hs.txt

blue "Extracting Arabeyes vector fonts ..."
tar xjvf aefonts.tar.bz2 && rm aefonts.tar.bz2
mv ae_fonts1-1.1/FS/*.ttf .
mv ae_fonts1-1.1/AGA/*.ttf .
mv ae_fonts1-1.1/MCS/*.ttf .
mv ae_fonts1-1.1/AAHS/*.ttf .
mv ae_fonts1-1.1/Kasr/*.ttf .
mv ae_fonts1-1.1/Shmookh/*.ttf .
mv ae_fonts1-1.1/license.txt . && rm -rf ae_fonts1-1.1/

blue "Extracting Society of Biblical Literature Hebrew font ..."
unzip SBLHebrew-Distributionv107.zip -d SBLHebrew && rm SBLHebrew-Distributionv107.zip
mv SBLHebrew/*.ttf . && rm -rf SBLHebrew

blue "Extracting Meltho Syriac fonts ..."
tar xzvf melthofonts-1.21.tar.gz && rm melthofonts-1.21.tar.gz
mv melthofonts-1.21/*.otf . && rm -rf melthofonts-1.21

blue "Extracting Dhivehi fonts ..."
unzip -j fonts.zip && rm fonts.zip

blue "Extracting five Devanagari Unicode OpenType fonts ..."
unzip -j Raghu.zip && rm Raghu.zip
#
unzip GISTYogeshN.zip && rm GISTYogeshN.zip
#mkdir CDAC_GIST_Open_Type_Font && mv "C-DAC GIST Open Type Font.pdf" "Open Type Fonts.doc" CDAC_GIST_Open_Type_Font
rm -f rm C-DAC\ GIST\ Open\ Type\ Font.pdf Open\ Type\ Fonts.doc
unzip surekh.zip && rm surekh.zip
unzip gargi.zip && rm gargi.zip
unzip janahindi.zip && rm janahindi.zip

blue "Extracting Jana Marathi font ..."
unzip JanaMarathi.zip && rm JanaMarathi.zip

blue "Extracting the Saab Gurmukhi (Punjabi) Unicode font ..."
unzip saab.0.91.zip && rm saab.0.91.zip

blue "Extracting Jana Kannada Unicode font ..."
unzip JanaKannada.zip && rm JanaKannada.zip

blue "Extracting FSF India's Malayalam project font ..."
gzip -d malayalam.ttf.gz

blue "Extracting Jana Malayalam font ..."
unzip JanaMalayalam.zip && rm JanaMalayalam.zip

blue "Extracting Oriya font from sarovar.org ..."
gzip -d utkal.ttf.gz

blue "Extracting Jana Tamil font ..."
unzip JanaTamil.zip && rm JanaTamil.zip

blue "Attempting to download Telugu Pothana2000 font ..."
unzip Pothana2k.zip -d Pothana2k && rm Pothana2k.zip
mv Pothana2k/*.ttf . && rm -rf Pothana2k

blue "Extracting Tibetan Machine Uni font from THDL ..."
unzip TibetanMachineUnicodeFont.zip && rm TibetanMachineUnicodeFont.zip

blue "Extracting KhmerOS Khmer Unicode fonts ..."
unzip All_KhmerOS_31.zip && rm All_KhmerOS_31.zip
rm LICENSE readme.txt

blue "Extracting Saysettha Lao Opentype font ..."
unzip saysettha_ot.zip && rm saysettha_ot.zip
mv "Saysettha OT.ttf" SaysetthaOT.ttf

blue "Extracting Paul Morrow's Philippine font ... "
unzip tagdoc93.zip && rm tagdoc93.zip
rm TagDoc93.rtf

blue "Extracting Valentium Tai Le font from fixedsys.org ..."
unzip taileval.zip && rm taileval.zip
rm readme.txt

blue "Extracting David Perry's Cardo Unicode font for classicists ..."
unzip cardo98.zip && rm cardo98.zip
rm install2.txt Manual98a.pdf

blue "Extracting Firefly's AR PL New Song font ..."
tar xzvf fireflysung-1.3.0.tar.gz && rm fireflysung-1.3.0.tar.gz
mv fireflysung-1.3.0/fireflysung.ttf . && rm -rf fireflysung-1.3.0

blue "Extracting Debian Taiwan's Arphic ukai and uming fonts ..."
tar xzvf ttf-arphic-uming_0.1.20060928.orig.tar.gz && rm ttf-arphic-uming_0.1.20060928.orig.tar.gz
mv ttf-arphic-uming-0.1.20060928/uming.ttf . && rm -rf ttf-arphic-uming-0.1.20060928
tar xzvf ttf-arphic-ukai_0.1.20060928.orig.tar.gz && rm ttf-arphic-ukai_0.1.20060928.orig.tar.gz
mv ttf-arphic-ukai-0.1.20060928/ukai.ttf . && rm -rf ttf-arphic-ukai-0.1.20060928

blue "Extracting Japanese Sazanami Mincho and Gothic fonts ..."
tar xjvf sazanami-20040629.tar.bz2 && rm sazanami-20040629.tar.bz2
mv sazanami-20040629/*.ttf . && rm -rf sazanami-20040629

blue "Extracting SIL Yi Unicode font ..."
unzip silyi.zip && rm silyi.zip
mv SILYI/silyi.ttf . && rm -rf SILYI

blue "Extracting the Korean Un fonts package ..."
tar xzvf un-fonts-core-1.0.tar.gz && rm un-fonts-core-1.0.tar.gz
mv un-fonts/*.ttf . && rm -rf un-fonts

blue "The ThaiFonts-Scalable fonts need to be built using fontforge. Hope you have it!"
tar xzvf thaifonts-scalable-0.4.3.1.tar.gz && rm thaifonts-scalable-0.4.3.1.tar.gz
cd thaifonts-scalable-0.4.3.1/ && ./configure && make
mv nf/*.ttf ..
mv nectec/*.ttf ..
mv tlwg/*.ttf ..
cd ..
rm -rf thaifonts-scalable-0.4.3.1

green "=== END OF SCRIPT ==="
green "=== END OF SCRIPT ==="
green "=== END OF SCRIPT ==="

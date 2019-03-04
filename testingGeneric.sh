cd ~/orkspace/McPAD/

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allGeneric.pcap -f "dst port 80" -n -1 -c AVG

# Usage
# $1 RootDirectory (with the -r option)
# $2 libpcap filter (with the -f option)
# $3 Number of test packets (with the -n option. -1 lets McPAD analyze all the packets into the file)
# $4 Combination rule (with the -c option. Possibilities are MIN, MAX, AVG, PROD, MAJ)


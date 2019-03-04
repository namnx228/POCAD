cd ~/orkspace/McPAD/

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TrainMcPAD -r ~/orkspace/McPAD/RootDirectory -f 0.01 -n 7000

#Usage
# $1 Root Directory (with the -r option)
# $2 Desired false positive rate (with the -f option)
# $3 Number of validation packets (with the -n option. This is the number of packets used to estimate thresholds)


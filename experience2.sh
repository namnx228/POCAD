cd ~/orkspace/McPAD/
# training 1%
java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TrainMcPAD -r ~/orkspace/McPAD/RootDirectory -f 0.01 -n 7000 > experience_result/training_001

# test Generic 1%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allGeneric.pcap -f "dst port 80" -n -1 -c MAX > experience_result/allGeneric_001

# test shell code 1%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allShellCode.pcap -f "dst port 80" -n -1 -c MAX > experience_result/shellCode_001

# test morphed 1%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/all_morphed_shellcode_attacks_payloads.pcap -f "dst port 80" -n -1 -c MAX > experience_result/morphed_001

# test false positive 1%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/test.pcap -f "dst port 80" -n -1 -c MAX > experience_result/false_001

# training 0.5%
java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TrainMcPAD -r ~/orkspace/McPAD/RootDirectory -f 0.005 -n 7000 > experience_result/training_0005

# test Generic 0.5%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allGeneric.pcap -f "dst port 80" -n -1 -c MAX > experience_result/allGeneric_0005

# test shell code 0.5%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allShellCode.pcap -f "dst port 80" -n -1 -c MAX > experience_result/shellCode_0005

# test morphed 0.5%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/all_morphed_shellcode_attacks_payloads.pcap -f "dst port 80" -n -1 -c MAX > experience_result/morphed_0005

# test false positive 0.5%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/test.pcap -f "dst port 80" -n -1 -c MAX > experience_result/false_0005

# training 0.2%
java -Xmx3G -Djava.library.path=./v -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TrainMcPAD -r ~/orkspace/McPAD/RootDirectory -f 0.002 -n 7000 > experience_result/training_0002

# test Generic 0.2%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allGeneric.pcap -f "dst port 80" -n -1 -c MAX > experience_result/allGeneric_0002

# test shell code 0.2%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allShellCode.pcap -f "dst port 80" -n -1 -c MAX > experience_result/shellCode_0002

# test morphed 0.2%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/all_morphed_shellcode_attacks_payloads.pcap -f "dst port 80" -n -1 -c MAX > experience_result/morphed_0002

# test false positive 0.2%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/test.pcap -f "dst port 80" -n -1 -c MAX > experience_result/false_0002

# training 5%
java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TrainMcPAD -r ~/orkspace/McPAD/RootDirectory -f 0.05 -n 7000 > experience_result/training_005

# test Generic 5%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allGeneric.pcap -f "dst port 80" -n -1 -c MAX > experience_result/allGeneric_005

# test shell code 5%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allShellCode.pcap -f "dst port 80" -n -1 -c MAX > experience_result/shellCode_005

# test morphed 5%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/all_morphed_shellcode_attacks_payloads.pcap -f "dst port 80" -n -1 -c MAX > experience_result/morphed_005

# test false positive 5%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/test.pcap -f "dst port 80" -n -1 -c MAX > experience_result/false_005

# training 2%
java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TrainMcPAD -r ~/orkspace/McPAD/RootDirectory -f 0.02 -n 7000 > experience_result/training_002

# test Generic 2%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allGeneric.pcap -f "dst port 80" -n -1 -c MAX > experience_result/allGeneric_002

# test shell code 2%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/allShellCode.pcap -f "dst port 80" -n -1 -c MAX > experience_result/shellCode_002

# test morphed 2%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/all_morphed_shellcode_attacks_payloads.pcap -f "dst port 80" -n -1 -c MAX > experience_result/morphed_002

# test false positive 2%

java -Xmx3G -Djava.library.path=./ -cp ./gnu.getopt.jar:./jpcap.jar:./libsvm.jar:./out/McPAD.jar edu.gatech.mcpad.core.TestMcPAD -r ~/orkspace/McPAD/RootDirectory/ -t ~/orkspace/McPAD/RootDirectory/pcap/test.pcap -f "dst port 80" -n -1 -c MAX > experience_result/false_002


.SUFFIXES: .java .class
.java.class:
	javac -classpath src/ -d ./ -g $*.java

CLASSES = \
	src/edu/wisc/cs/sdn/simpledns/packet/DNS.java \
	src/edu/wisc/cs/sdn/simpledns/packet/DNSQuestion.java \
        src/edu/wisc/cs/sdn/simpledns/packet/DNSRdataAddress.java \
        src/edu/wisc/cs/sdn/simpledns/packet/DNSRdataBytes.java \
        src/edu/wisc/cs/sdn/simpledns/packet/DNSRdata.java \
        src/edu/wisc/cs/sdn/simpledns/packet/DNSRdataName.java \
        src/edu/wisc/cs/sdn/simpledns/packet/DNSRdataString.java \
        src/edu/wisc/cs/sdn/simpledns/packet/DNSResourceRecord.java \
	src/edu/wisc/cs/sdn/simpledns/SimpleDNS.java \

default: classes

classes: $(CLASSES:.java=.class)

run: 
	java edu.wisc.cs.sdn.simpledns.SimpleDNS -r 198.41.0.4 -e ec2.csv

clean:
	$(RM) -r edu/

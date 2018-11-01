# Reed-Muller coder-decoder

This is my Reed-Muller code coder and decoder implementation. Decoder uses fast Hadamard transforms.
You give an input, it is coded in Reed-Muller, transmitted through a noisy channel and then decoded. The result is then presented to you.

### Installation

To compile and prepare the program for running use these commands

```
> buildReedMuller
```

### Running

To run the program use

```
> runReedMuller <commandLineArguments>
```

The command line arguements are these:

##### Required
* -m <INTEGER>, e.g. "-m 4". This is the m for code generation.
* -e <DOUBLE[0,1]> "-e 0.1". This is the channels error rate. 

if no valid flags are entered after the required arguments, all the text is fed into the program and then printed as it was decoded.

#### Optional (only one can be used per command)
* -f <FILE_NAME>, e.g. "-f test.txt". This give program the file and outputs it with ".out" extension.
* -b <binary vector representation> "-b 01010001111". This prints out the binary representation of the decoded vector

#### Examples
To run program on input text you could write
```
> runReedMuller -m 4 -e 0.1 This is my text
```

To run on a chosen file
```
> runReedMuller -m 4 -e 0.1 myFile.txt
```

To run on a binary vector
```
> runReedMuller -m 3 -e 0.1 -b 01010101111
```

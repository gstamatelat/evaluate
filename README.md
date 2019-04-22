# Evaluate

A Java 8 utility to evaluate various types of data.

## Usage

```
./evaluate truth.txt data1.txt data2.txt ...
```

The script will evaluate the `data*.txt` inputs against the `truth.txt` file
using any compatible methods based on the format of the files. The format of the
files is automatically determined based on the hash value in the first line of
each file.

### Value Map

```
# values
cat 0.6
dog 0.1
bear 0.3
```

A map of elements to decimal values.

### Rank List

```
# ranks
dog
bear
cat
```

An ordered list of ranked elements.

### Tied Rank List

```
# tie-ranks
dog
bear cat
```

An ordered list of ranked elements with possible ties.

### Partition

```
# partition
dog
bear cat
```

A partition of elements in groups.

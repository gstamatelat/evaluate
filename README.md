# Evaluate

A Java 8 utility to evaluate various types of data.

## Usage

```
./evaluate truth.txt data1.txt data2.txt ...
```

Each file can be one of two formats: rank list or value map. The script will
determine the format based on the hash value in the first line of the file,
which may be **ranks** or **values** respectively.

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

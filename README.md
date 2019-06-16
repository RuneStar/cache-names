# cache-names [![Discord](https://img.shields.io/discord/384870460640329728.svg?logo=discord)](https://discord.gg/G2kxrnU)

[**View all**](https://github.com/RuneStar/cache-names/blob/master/names.tsv)

Some files in the Old School RuneScape cache contain a hash of their name. 
The only way to find out the names of these files is to reverse the hashes.

The following hash function is used:

```java
static int hash(byte[] bytes) {
    int h = 0;
    for (byte b : bytes) {
        h = h * 31 + b;
    }
    return h;
}
```

It is equivalent to Java [String::hashCode](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#hashCode--) for ASCII-only inputs
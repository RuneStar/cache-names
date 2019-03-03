[![Discord](https://img.shields.io/discord/384870460640329728.svg?logo=discord)](https://discord.gg/G2kxrnU)

[**View all**](https://github.com/RuneStar/cache-names/blob/master/names.tsv)

Some files in the Old School RuneScape cache contain a hash of their name. 
It is hashed using Java [String::hashCode](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#hashCode--),
a non-cryptographic hash function which produces a 32-bit value.
The only way to find out the names of these files is to reverse the hashes.
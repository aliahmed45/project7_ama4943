// Written by Ali Ahmed

The project is having difficulty with using scanner to read files. THe bug appeared an hour before the deadline
and I don't know what to do. I thought i had previously fixed the bug with the FileInputStream, but then randomly
Scanner broke again.


Parameters for using program:
Requires command line paramters:
1st) path/to/file
2nd) phrase length - must be an integer

Files documents cannot be empty, nor can they have fewer words than the 2nd parameter
Sadly the 2nd parameter is limited by how Scanner delimits the words. Currently, Scanner is broken and I have no idea
why it stopped working. See note at begining. Addtionally, words that are not contiguous by alpha-numeric will be broken
apart into new words. For example "Ali's" will be broken into two words.

Implementation strategy:
Use a HashMap to keep a single large dictionary for all the words in all the files.
Use List of HashMaps to keep a dictionary for each individual file. Each individual file Hashmap is key by unique word
values in the file, and then the next (N-1) words are put into an arraylist to serve as the value to the key pair.
If the word already exists in the file, the value arraylist will simply append the next five words to current arraylist
in value.

The searching function does N choose 2 compares, and looks to see if the first word is matched before checking if the
N-level string to be matched.


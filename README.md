# SC3020-Project-1

To figure out the block size setting of your computer, run the following commands:

```bash
diskutil info /
```

For Macbook Pro M1, the block size is 4096 Bytes

## Goal
### (1) Task 1: Design and implement the storage component based on the settings described in Part 1 and store the data (which is about NBA games and will be described in Part 4).
- Describe the content of a record, a block, and a database file;
- Report the following statistics: the size of a record; the number of
records; the number of records stored in a block; the number of blocks
for storing the data

Record Fields:
- GAME_DATE_EST	
- TEAM_ID_home	
- PTS_home	
- FG_PCT_home	
- FT_PCT_home	
- FG3_PCT_home	
- AST_home	
- REB_home	
- HOME_TEAM_WINS

### (2) Task 2: Design and implement the indexing component based on the settings described in Part 1 and build a B+ tree on the data described in Task 1 with the attribute "FG_PCT_home" as the key.
- Report the following statistics: 
  - the parameter n of the B+ tree; 
  - the number of nodes of the B+ tree; 
  - the number of levels of the B+ tree; 
  - the content of the root node (only the keys);

### (3) Task 3: Search those movies with the attribute “FG_PCT_home” from 0.5 to 0.8 (both inclusively) using the B+ tree
- Report the following statistics: 
  - the number of index nodes the process accesses; 
  - the number of data blocks the process accesses; 
  - the average of “FG3_PCT_home” of the records that are returned; 
  - the running time of the retrieval process; 
  - the number of data blocks that would be accessed by a brute-force linear scan method (i.e., it scans the data blocks one by one) and its running time (for comparison)

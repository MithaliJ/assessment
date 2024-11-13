# Flow Log Tagger Program

This program processes flow log data by tagging each entry based on a `dstport` and `protocol` lookup table, then outputs counts of each tag and port/protocol combination.

### Features
- Tags flow log entries based on `dstport` and `protocol` using a predefined lookup table.
- Outputs:
  - **Tag Counts**: Number of matches for each tag.
  - **Port/Protocol Combination Counts**: Count of each unique `dstport` and `protocol` combination.

### Assumptions
1. **Protocol Codes**:
   - `6` represents `tcp`.
   - `17` represents `udp`.
   - '1' represents 'icmp'
   - Other values are mapped to `"other"` for protocols.
2. **Untagged Entries**: If no tag is found for a given `dstport` and `protocol` combination, it is categorized as `Untagged`.
3. **Case Insensitivity**: Matching for `dstport` and `protocol` is case-insensitive.
4. **Input File Format**:
   - The flow log data file should be a plain ASCII text file, with fields separated by whitespace, and each row having at least 14 columns.
   - The lookup table should be a CSV file with columns `dstport`, `protocol`, and `tag`.
5. **Output File Format**:
   - The results are output to a plain text file, `output_results.txt`, containing tag counts and port/protocol combination counts.
6. **Default Protocol Mapping**: Only protocols `tcp` and `udp` are mapped explicitly, other protocol numbers are grouped as `"other"`.

### Requirements
- **Java 8 or above**
- Flow log file and lookup table in the correct format

### Files
- `FlowLogTagger.java`: The main program file.
- `lookup_table.csv`: Lookup table file containing columns `dstport`, `protocol`, and `tag`.
- `flow_log.txt`: Sample flow log file containing data entries.
- `output_results.txt`: Output file generated after running the program.

### How to Run the Program

1. **Compile the Program**:
   ```bash
   javac FlowLogTagger.java
   ```

2. **Run the Program**:
   ```bash
   java FlowLogTagger
   ```

   Ensure the following files are in the same directory as `FlowLogTagger.java`:
   - `lookup_table.csv` (lookup table file)
   - `flow_log.txt` (flow log file)

3. **View Output**:
   - The program generates `output_results.txt` in the current directory, containing:
     - Counts for each tag (`Tag Counts` section)
     - Counts for each port/protocol combination (`Port/Protocol Combination Counts` section)

### Example Files

#### `lookup_table.csv`
```csv
dstport,protocol,tag
25,tcp,sv_P1
443,tcp,sv_P2
23,tcp,sv_P1
110,tcp,email
993,tcp,email
143,tcp,email
```

#### `flow_log.txt`
```plaintext
2 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 443 49153 6 25 20000 1620140761 1620140821 ACCEPT OK
2 123456789012 eni-4d3c2b1a 192.168.1.100 203.0.113.101 23 49154 6 15 12000 1620140761 1620140821 REJECT OK
```

After running the program, the output in `output_results.txt` would look something like this:

#### `output_results.txt`
```plaintext
Tag Counts:
Tag,Count
sv_P2,1
sv_P1,1
email,1
Untagged,9

Port/Protocol Combination Counts:
Port,Protocol,Count
443,tcp,1
23,tcp,1
110,tcp,1
993,tcp,1
143,tcp,1
...
```

### Troubleshooting
- **File Not Found**: Ensure `lookup_table.csv` and `flow_log.txt` are in the same directory as `FlowLogTagger.java` or provide their full paths in the code if located elsewhere.
- **Invalid Format**: Verify that `lookup_table.csv` and `flow_log.txt` follow the formats specified above.
- **Unsupported Protocols**: The program only differentiates `tcp`, `udp`, `icmp` and treats others as `"other"`.


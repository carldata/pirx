# Pirx application

Real time data analytics. This application analyzes data on kafka topic using various approaches.

Currently implemented:

 * [x] Predicting traffic on a Kafka topic in the next second
 * [ ] Predict 1sec traffic 10 seconds from now
 

## Running test
 
 ```bash
sbt assembly
java -jar target/scala-2.12/pirx.jar --kafka=localhost:9092
 ```
 
# Redistributing

Pirx source code is distributed under the Apache-2.0 license.

**Contributions**

Unless you explicitly state otherwise, any contribution intentionally submitted
for inclusion in the work by you, as defined in the Apache-2.0 license, shall be
licensed as above, without any additional terms or conditions.
t
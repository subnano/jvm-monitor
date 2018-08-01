[![Build Status](https://travis-ci.com/subnano/jvm-monitor.svg?branch=master)](https://travis-ci.com/subnano/jvm-monitor)

# JVM Monitor
Monitors and records JVM garbage collection activity.

### TODO
- use settings to schedule different monitors at different intervals
- add external dependencies on nano-core (for clock) and nano-kx
- log recording intervals / settings
- periodically output some stats
- improve test coverage
- limit use of 'sun' source code
- improve efficiency of monitoring - should use PerfDataBuffer directly
- kdb writing should be async

## Build 
`mvn clean package`

## Create Docker image
`docker build . -t alialmahari/geode-server`

## Run Docker compose file
`docker compose up`

## gfsh
- `connect --locator=geode.local[11235]`
- `list members`
- `list regions`
- `get --region=Numbers --key=1 --key-class=java.lang.Long`


## Links
- https://github.com/quitada41/Geode-Multisite-handson
- https://geode.apache.org/docs/guide/114/tools_modules/gfsh/quick_ref_commands_by_area.html
- https://geode.apache.org/docs/guide/114/reference/topics/elements_ref.html#topic_7B1CABCAD056499AA57AF3CFDBF8ABE3


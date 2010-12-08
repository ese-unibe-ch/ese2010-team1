#!/bin/sh

qa=$(dirname $(dirname "$(pwd)/$0"))
package="$qa/app/models/fraudpointscale"
config="$qa/app/META-INF/services/models.fraudpointscale.FraudPointRule"

rm "$config"

for class in $(ls "$package"); do
	rule=$(grep -P "class \w+ extends FraudPointRule" "$package/$class")
	if [ "$rule" ]; then
		class=$(basename "$class" .java)
		echo "Found service: $class"
		echo "models.fraudpointscale.$class" >> "$config"
	fi
done

# react-native-rfid-chainway-c72

handled module for chainway c72

## Installation

```sh
npm install react-native-rfid-chainway-c72
```

## Usage


```js
import React from 'react';
import {
  StyleSheet,
  Text,
  View,
  Alert,
  Button,
  TextInput,
} from 'react-native';

import {
  deInitializeReader,
  readPower,
  readSingleTag,
  startReadingTags,
  stopReadingTags,
  initializeReader,
  powerListener,
  tagListener,
  writeDataIntoEpc,
} from 'react-native-rfid-chainway-c72';
const App = () => {
  const [isReading, setIsReading] = React.useState();
  const [powerState, setPowerState] = React.useState('');
  const [tags, setTags] = React.useState([]);
  const [value, setValue] = React.useState("123456789012345678901234");

  const showAlert = (title: string, data: string | undefined) => {
    Alert.alert(
      title,
      data,
      [{ text: 'OK', onPress: () => console.log('OK Pressed') }],
      { cancelable: false }
    );
  };

  const eventListenerPower = (data: React.SetStateAction<string>) => {
    console.log(data);
    setPowerState(data);
  };

  const eventListenerTag = (data: any[]) => {
    console.log('tag', data);
    setTags((tags) => tags.concat(data[0]));
  };

  React.useEffect(() => {
    loadData();

    return () => deInitializeReader();
  }, []);

  const loadData = async () => {
    console.log('loadData');
    await initializeReader();
    powerListener(eventListenerPower);
    tagListener(eventListenerTag);
  };

  const handlerReadPower = async () => {
    try {
      let result = await readPower();
      showAlert('SUCCESS', `The result is ${result}`);
      console.log(result);
    } catch (error) {
      showAlert('FAILED', error.message);
    }
  };

  const handlerScanSingleTag = async () => {
    try {
      let result = await readSingleTag();
      showAlert('SUCCESS', `The result is ${result}`);
      console.log(result);
    } catch (error) {
      showAlert('FAILED', error.message);
    }
  };

  const handlerStartReading = () => {
    startReadingTags(function success(message) {
      console.log({ tags, message });
      setIsReading(message);
    });
  };

  const handlerStopReading = () => {
    stopReadingTags(function success(message) {
      console.log({ message })
      setIsReading(false);
    });

    /**
     * When I stop scanning I immediately return the tags in my state
     * (You could render a view or do whatever you want with the data)
     */
    console.log({ tags });
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <View>
        <Text>{powerState}</Text>
      </View>

      <View style={{ marginVertical: 20 }}>
        <Button
          style={{ margin: 10 }}
          onPress={() => handlerReadPower()}
          title="Read Power"
        />
      </View>

      <View style={{ marginVertical: 20 }}>
        <Button
          style={{ margin: 10 }}
          onPress={() => handlerScanSingleTag()}
          title="Read Single Tag"
        />
      </View>

      <View style={{ marginVertical: 20 }}>
        <Button
          disabled={isReading}
          style={{ margin: 10 }}
          onPress={() => handlerStartReading()}
          title="Start Bulk Scan"
        />
      </View>

      <View style={{ marginVertical: 20 }}>
        <Button
          disabled={!isReading}
          style={{ margin: 10 }}
          onPress={() => handlerStopReading()}
          title="Stop Bulk Scan"
        />
      </View>

      <TextInput
        style={styles.input}
        onChangeText={value => setValue(value)}
        maxLength={24}
        value={value}
        placeholder="Digite aqui..."
      />

      <View style={{ marginVertical: 20 }}>
        <Button
          style={{ margin: 10 }}
          onPress={() => writeDataIntoEpc(value)}
          title="Write Data"
        />
      </View>

    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 20,
    backgroundColor: '#f0f0f0',
  },
  input: {
    height: 40,
    width: 250,
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 5,
    paddingHorizontal: 10,
    backgroundColor: '#fff',
  },
});

export default App;

```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)

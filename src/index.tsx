import NativeRfidChainwayC72 from './NativeRfidChainwayC72';
import { NativeEventEmitter, NativeModules } from 'react-native';

const eventEmitter = new NativeEventEmitter(NativeModules.RfidChainwayC72);

type Callback = (args: any[]) => void;
type AddListener = (cb: (args: any[]) => void) => void;

export const powerListener = function (listener: AddListener) {
  eventEmitter.addListener('UHF_POWER', listener);
};

export const tagListener = function (listener: AddListener) {
  eventEmitter.addListener('UHF_TAG', listener);
};

export function multiply(a: number, b: number): number {
  return NativeRfidChainwayC72.multiply(a, b);
}

export function initializeReader(): Promise<any> {
  return NativeRfidChainwayC72.initializeReader();
}

export function deInitializeReader(): Promise<any> {
  return NativeRfidChainwayC72.deInitializeReader();
}

export function readSingleTag(): Promise<any> {
  return NativeRfidChainwayC72.readSingleTag();
}

export function startReadingTags(callback: Callback): void {
  return NativeRfidChainwayC72.startReadingTags(callback);
}

export function stopReadingTags(callback: Callback): void {
  return NativeRfidChainwayC72.stopReadingTags(callback);
}

export function readPower(): Promise<any> {
  return NativeRfidChainwayC72.readPower();
}

export function changePower(powerValue: number): Promise<any> {
  return NativeRfidChainwayC72.changePower(powerValue);
}

export function writeDataIntoEpc(epc: string): Promise<any> {
  return NativeRfidChainwayC72.writeDataIntoEpc(epc);
}

export function findTag(findEpc: string, callback: Callback): void {
  return NativeRfidChainwayC72.findTag(findEpc, callback);
}

// export function clearAllTags(): Promise<any> {
//   return NativeRfidChainwayC72.clearAllTags();
// }

export default {
  multiply,
  powerListener,
  tagListener,
  initializeReader,
  deInitializeReader,
  readSingleTag,
  startReadingTags,
  stopReadingTags,
  readPower,
  // changePower,
  // clearAllTags,
};

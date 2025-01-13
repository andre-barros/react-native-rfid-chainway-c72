import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

type Callback = (...args: any[]) => void;

export interface Spec extends TurboModule {
  multiply(a: number, b: number): number;
  initializeReader(): Promise<any>;
  deInitializeReader(): Promise<any>;
  readSingleTag(): Promise<any>;
  startReadingTags(callback: Callback): void;
  stopReadingTags(callback: Callback): void;
  readPower(): Promise<any>;
  changePower: (powerValue: number) => Promise<any>;
  writeDataIntoEpc: (epc: string) => Promise<any>;
  findTag(findEpc: string, callback: Callback): void;
  // clearAllTags(): Promise<any>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RfidChainwayC72');

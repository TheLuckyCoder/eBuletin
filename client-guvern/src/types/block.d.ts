import { ITransaction } from "./transaction";

export interface IBlock {
  blockNumber: number;
  timestamp: Date;
  transactions: ITransaction[];
  parentHash: string;
  nounce: string;
  hash: string;
}
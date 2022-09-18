export interface IIdCard {
  cnp: number;
  lastName: string;
  firstName: string;
  birthLocation: string;
  sex: "M" | "F";
  series: "SB";
  seriesNumber: string;
  validity: string;
  issuedBy: string;
}

export interface ISignature {
  v: string;
  r: string;
  s: string;
}

export interface ITransactionInformation {
  idCard: IIdCard;
}


export interface ITransaction {
  hash: string;
  sender: string;
  receiver: string;
  data: {
    information: ITransactionInformation;
  };
  signature: ISignature;
  nounce: number;
  [key: string]: any;
}

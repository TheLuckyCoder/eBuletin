export interface IIdCard {
  cnp: number;
  lastName: string;
  firstName: string;
  birthLocation: string;
  sex: "M" | "F";
  series: string;
  seriesNumber: string;
  validity: string;
  issuedBy: string;
  address: string;
}

export interface IMedicalCard {
  lastName: string;
  firstName: string;
  insuranceCode: number;
  documentNumber: number;
  expiryDate: string;
}

export interface IDriverLicense {
  lastName: string;
  firstName: string;
  placeAndDateOfBirth: string;
  issueDate: string;
  expirationDate: string;
  issuedBy: string;
  licenseNumber: string;
  validFrom: string;
  validUntil: string;
  categories: string[];
}

export interface ISignature {
  v: string;
  r: string;
  s: string;
}

export interface ITransactionInformation {
  idCard?: IIdCard;
  medicalCard?: IMedicalCard;
  driverLicense?: IDriverLicense;
}

export interface ITransactionData {
  information?: ITransactionInformation;
}

export interface ITransaction {
  hash: string;
  sender: string;
  receiver: string;
  data: ITransactionData;
  signature: ISignature;
  nonce: number;
  [key: string]: any;
}

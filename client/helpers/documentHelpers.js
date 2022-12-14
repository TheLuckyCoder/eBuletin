import { getErrorMessage } from "./general";

export const getBirthDateFromCnp = (cnpNr) => {
  const cnp = String(cnpNr);
  const year = "20" + cnp.substring(1, 3);
  const month = cnp.substring(3, 5);
  const day = cnp.substring(5, 7);
  return `${day} ${month} ${year}`;
};

export const handleSuccess = (data, setState) => {
  setState({
    data,
    loading: false,
    error: null,
  });
};

export const handleError = (e, setState) => {
  setState({
    data: null,
    loading: false,
    error: getErrorMessage(e),
  });
};

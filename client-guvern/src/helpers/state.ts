export const handleSuccess = (data: any, setState: (data: any) => void) => {
  setState({
    data,
    loading: false,
    error: null,
  });
};

export const handleError = (error: string, setState: (data: any) => void) => {
  setState({
    data: [],
    loading: false,
    error,
  });
};

export const getErrorMessage = (error: any): string => {
  console.log(error);
  return (
    (error?.response?.data?.message as string) ||
    (error?.response?.data as string) ||
    "Something went wrong"
  );
};

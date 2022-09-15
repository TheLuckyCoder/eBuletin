export interface IRequestState<T> {
  data: T | [];
  loading: boolean;
  error: string | null;
}

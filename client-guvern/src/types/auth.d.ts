
interface LoginInput {
    username: string;
    password: string;
}

interface RegisterInput {
    email: string;
    username: string
    password: string;
    confirmPassword: string;
}

interface IUser {
    id: string;
    email: string;
    username: string;
    createdAt: string;
}
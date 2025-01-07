import { deleteUser_e2eTest } from './modules/delete_user';
import { loginUser_e2eTest } from './modules/login_user';
import { logoutUser_e2eTest } from './modules/logout_user';
import { manageSession_e2eTest } from './modules/manage_session';
import { participateSession_e2eTest } from './modules/participate_session';
import { registerUser_e2eTest } from './modules/register_user';
import { userAccount_e2eTest } from './modules/user_account';

loginUser_e2eTest();
logoutUser_e2eTest();
registerUser_e2eTest();
userAccount_e2eTest();
deleteUser_e2eTest();
participateSession_e2eTest();
manageSession_e2eTest();


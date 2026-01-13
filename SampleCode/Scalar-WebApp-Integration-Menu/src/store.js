import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./features/auth/authSlice";

import auditFolderSlice from "./features/auditfolder/auditFolderSlice";
import tokenSlice from "./features/auth/tokenSlice";

const store = configureStore({
  reducer: {
    auth: authReducer,
    folder: auditFolderSlice,
    token: tokenSlice,
  },
});

export default store;

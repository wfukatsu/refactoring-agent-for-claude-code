import { Tuple, configureStore } from "@reduxjs/toolkit";
import { combineReducers } from "redux";
import { persistReducer, persistStore } from "redux-persist";
import storage from "redux-persist/lib/storage";
import { createLogger } from "redux-logger";
import authSlice from "./reducerSlice/authSlice";
import folderAndFileSlice from "./reducerSlice/folderAndFileSlice";
import tokenSlice from "./reducerSlice/tokenSlice";

const logger = createLogger();

const reducer = combineReducers({
  auth: authSlice,
  folderAndFileSlice: folderAndFileSlice,
  token: tokenSlice,
});

const persistConfig = {
  key: "root",
  storage,
};

const persistedReducer = persistReducer(persistConfig, reducer);

export const store = configureStore({
  reducer: persistedReducer,
  middleware: () => new Tuple(logger),
});

export const persistor = persistStore(store);

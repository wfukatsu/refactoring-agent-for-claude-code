import "./App.css";
import {
  BrowserRouter,
  Routes,
  Route,
  HashRouter,
  Navigate,
} from "react-router-dom";
// import Dashboard from "./pages/Dashboard";
import Dashboard from "./pages/Dashboard";
// import Home from "./pages/Home";
import ViewEventHistory from "./components/ViewEventHistory";
import LogoutPage from "./features/auth/LogoutPage";
import { useSelector } from "react-redux";
import { useEffect } from "react";
// import FolderAuditorDashboard from "./features/auditfolder/FolderAuditorDashboard";

import { useTranslation } from "react-i18next";
import detectBrowserLanguage from "detect-browser-language";

function App() {
  const { user } = useSelector((Store) => Store.auth);

  const { i18n } = useTranslation();

  useEffect(() => {
    const browserLanguage = detectBrowserLanguage();
    if (user !== null) {
      // i18n.changeLanguage(user.languageCode);
      // console.log("this",user.languageCode)
    } else {
      if (browserLanguage === "ja-JP") {
        i18n.changeLanguage("ja");
      } else {
        i18n.changeLanguage("en");
      }
    }
  }, []);

  return (
    <BrowserRouter>
      <Routes
        exact
        path={["/scalar", "/"]}
        element={
          user ? (
            <Navigate to="/scalar" replace />
          ) : (
            <Navigate to="/logout" replace />
          )
        }
      >
        <Route path="/" element={<Dashboard />} />
        <Route path="/scalar" element={<Dashboard />} />
        <Route path="/event-history" element={<ViewEventHistory />} />
        <Route path="/logout" element={<LogoutPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

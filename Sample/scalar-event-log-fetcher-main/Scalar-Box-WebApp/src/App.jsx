import React, { Suspense, lazy, useEffect } from "react";
import {
  BrowserRouter,
  HashRouter,
  Route,
  Routes,
  Navigate,
  useLocation,
  useNavigate,
} from "react-router-dom";
import Loder from "./common/Loader/Loder";
import { styled } from "@mui/material/styles";
import { useDispatch, useSelector } from "react-redux";
import { LOGOUT } from "../src/redux/reducerSlice/authSlice";
import ExternalAuditorViewitemAnderAuditSet from "./pages/ExternalAuditorPage/ExternalAuditorViewitemAnderAuditSet";

import ExternalAuditor from "./pages/ExternalAuditorPage/ExternalAuditor";
import { useTranslation } from "react-i18next";
import Sidemenu from "./pages/layout/Sidemenu";
import MainHeader from "./pages/layout/MainHeader";

const ViewItemUnderAuditSet = lazy(() =>
  import("./pages/ViewItemsUnderAudit/components/ViewItemUnderAuditSet")
);

const ViewitemsUnderAudit = lazy(() =>
  import("./pages/ViewItemsUnderAudit/ViewitemsUnderAudit")
);

const GenralUser = lazy(() => import("./pages/GenralUser/GenralUser"));

const LoginandSignup = lazy(() => import("./pages/auth/LoginandSignup"));

const UserRole = lazy(() => import("./pages/UserRole/UserRole"));

const AuditSet = lazy(() => import("./pages/AuditSet/AuditSet"));

const ItemViewFileandFolders = lazy(() =>
  import("./pages/AuditSet/components/ItemViewFileandFolders")
);

const AuditorsAndGroups = lazy(() =>
  import("./pages/AuditorsAndGroups/AuditorsAndGroups")
);

const ApplicationSetting = lazy(() =>
  import("./pages/ApplicationSetting/ApplicationSetting")
);

const ViewItemUnderAudit = lazy(() =>
  import("./pages/ViewItemsUnderAudit/ViewitemsUnderAudit")
);

const ViewAllEventHistory = lazy(() =>
  import("./pages/ViewAllEventHistory/ViewAllEventHistory")
);

function App() {
  const [open, setOpen] = React.useState(false);
  const user = useSelector((state) => state.auth.user);
  const dispatch = useDispatch();
  const { i18n } = useTranslation();

  console.log(user, "USER/////");

  const Main = styled("main")(({ theme }) => ({
    marginLeft: 0,
    flexGrow: 1,
    padding: theme.spacing(2),
    paddingTop: 15,
    backgroundColor: "#ffff",
    transition: theme.transitions.create("margin", {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
  }));

  // useEffect(() => {
  //   const handleBeforeUnload = (event) => {
  //     event.stopPropagation();
  //     event.preventDefault();

  //     console.log(
  //       "Refresh event BEFORE== ",
  //       event.returnValue,
  //       "=====",
  //       window.location
  //     );
  //     // window.location.href = "https://test7.jeeni.in/scalar-box";
  //     navigate("/scalar-box");

  //     // event.returnValue = "";

  //     console.log(
  //       "Refresh event AFTER== ",
  //       event.returnValue,
  //       "=====",
  //       window.location
  //     );
  //   };

  //   // Add event listener when component mounts
  //   window.addEventListener("beforeunload", handleBeforeUnload);

  //   // Clean up event listener when component unmounts
  //   return () => {
  //     window.removeEventListener("beforeunload", handleBeforeUnload);
  //   };
  // }, []);

  useEffect(() => {
   let languageCode = localStorage.getItem("LANGUAGE_CODE") ?? "en"
  //  console.log("language Code  App", languageCode)
    // if (user !== null) {
    //   console.log("INSIDE APP")
    //   const languageCode = user.languageCode;
      i18n.changeLanguage(languageCode);
    // }
  }, []);

  if (user === null) {
    return (
      <BrowserRouter>
        <Suspense fallback={<Loder />}>
          <Routes
            exact
            path={["/scalar-box", "/"]}
            element={<Navigate to="/scalar-box" replace />}
          >
            <Route exact path="/scalar-box" element={<LoginandSignup />} />
            <Route exact path="/" element={<LoginandSignup />} />
          </Routes>
        </Suspense>
      </BrowserRouter>
    );
  } else {
    if (user.userRoles.length === 1 && user.userRoles[0] === "GENERAL_USER") {
      console.log("GENERAL_USER");
      return (
        <>
          <HashRouter>
            <Sidemenu>
              <MainHeader />
              <Main>
                <Suspense fallback={<Loder />}>
                  <Routes
                    exact
                    path={["/viewitemunderaudit", "/"]}
                    element={<Navigate to="/viewitemunderaudit" replace />}
                  >
                    <Route
                      exact
                      path="/viewitemunderaudit"
                      element={<ViewitemsUnderAudit />}
                    />
                    <Route
                      exact
                      path="/viewitemunderaudit/viewitemunderauditset/:auditSetId/:auditSetName"
                      element={<ViewItemUnderAuditSet />}
                    />
                    <Route
                      path="/"
                      element={<Navigate to="/viewitemunderaudit" replace />}
                    />
                  </Routes>
                </Suspense>
              </Main>
            </Sidemenu>
          </HashRouter>
        </>
      );
    } else if (
      user.userRoles.length === 1 &&
      user.userRoles[0] === "EXTERNAL_AUDITOR"
    ) {
      return (
        <HashRouter>
          <Suspense fallback={<Loder />}>
            <Routes
              exact
              path={["/external", "/"]}
              element={<Navigate to="/external" replace />}
            >
              <Route exact path="/external" element={<ExternalAuditor />} />
              <Route
                exact
                path="/external/viewitemunderauditset/:auditSetId/:auditSetName"
                element={<ExternalAuditorViewitemAnderAuditSet />}
              />
              <Route path="/" element={<Navigate to="/external" replace />} />
            </Routes>
          </Suspense>
        </HashRouter>
      );
    } else {
      return (
        <HashRouter>
          <Sidemenu>
            <MainHeader />
            <Main>
              <Suspense fallback={<Loder />}>
                <Routes
                  exact
                  path={["/userrole", "/"]}
                  element={<Navigate to="/userrole" replace />}
                >
                  <Route exact path="/genraluser" element={<GenralUser />} />

                  <Route exact path="/userrole" element={<UserRole />} />
                  <Route
                    exact
                    path="/auditorsandgroups"
                    element={<AuditorsAndGroups />}
                  />
                  <Route exact path="/auditset" element={<AuditSet />} />
                  <Route
                    exact
                    path="/auditset/viewfolderandfiles/:auditSetId/:auditSetName"
                    element={<ItemViewFileandFolders />}
                  />
                  <Route
                    exact
                    path="/applicationsetting"
                    element={<ApplicationSetting />}
                  />
                  <Route
                    exact
                    path="/viewitemunderaudit"
                    element={<ViewitemsUnderAudit />}
                  />
                  <Route
                    exact
                    path="/viewitemunderaudit/viewitemunderauditset/:auditSetId/:auditSetName"
                    element={<ViewItemUnderAuditSet />}
                  />
                  <Route
                    exact
                    path="/viewalleventhistory"
                    element={<ViewAllEventHistory />}
                  />
                  <Route
                    path="/"
                    element={<Navigate to="/userrole" replace />}
                  />
                </Routes>
              </Suspense>
            </Main>
          </Sidemenu>
        </HashRouter>
      );
    }

    // return (
    //   <>
    //     {user ? (
    //       <Router>
    //         {user.userRoles.length === 1 &&
    //         user.userRoles[0] === "GENERAL_USER" ? (
    //           <>
    //             <MainLayout
    //               open={open}
    //               setOpen={setOpen}
    //               handleLogout={handleLogout}
    //             />
    //             <Suspense fallback={<Loder />}>
    //               <Main>
    //                 <Routes
    //                   exact
    //                   path="/viewitemunderaudit"
    //                   element={<Navigate to="/viewitemunderaudit" replace />}
    //                 >
    //                   <Route
    //                     exact
    //                     path="/viewitemunderaudit"
    //                     element={<ViewitemsUnderAudit open={open} />}
    //                   />
    //                   <Route
    //                     exact
    //                     path="/viewitemunderaudit/viewitemunderauditset/:auditSetId"
    //                     element={<ViewItemUnderAuditSet open={open} />}
    //                   />
    //                 </Routes>
    //               </Main>
    //             </Suspense>
    //           </>
    //         ) : user.userRoles.length === 1 &&
    //           user.userRoles[0] === "EXTERNAL_AUDITOR" ? (
    //           <>
    //             <Suspense fallback={<Loder />}>
    //               <Routes
    //                 exact
    //                 path="/external"
    //                 element={<Navigate to="/external" replace />}
    //               >
    //                 <Route
    //                   exact
    //                   path="/external"
    //                   element={<ExternalAuditor />}
    //                 />
    //                 <Route
    //                   exact
    //                   path="/external/viewitemunderauditset/:auditSetId"
    //                   element={<ExternalAuditorViewitemAnderAuditSet />}
    //                 />
    //               </Routes>
    //             </Suspense>
    //           </>
    //         ) : (
    //           <>
    //             <MainLayout
    //               open={open}
    //               setOpen={setOpen}
    //               handleLogout={handleLogout}
    //             />
    //             <Suspense fallback={<Loder />}>
    //               <Main>
    //                 <Routes
    //                   exact
    //                   path="/userrole"
    //                   element={<Navigate to="/scalar-box" replace />}
    //                 >
    //                   <Route
    //                     exact
    //                     path="/genraluser"
    //                     element={<GenralUser open={open} />}
    //                   />

    //                   <Route
    //                     exact
    //                     path="/userrole"
    //                     element={<UserRole open={open} />}
    //                   />
    //                   <Route
    //                     exact
    //                     path="/auditorsandgroups"
    //                     element={<AuditorsAndGroups open={open} />}
    //                   />
    //                   <Route
    //                     exact
    //                     path="/auditset"
    //                     element={<AuditSet open={open} />}
    //                   />
    //                   <Route
    //                     exact
    //                     path="/auditset/viewfolderandfiles/:auditSetId"
    //                     element={<ItemViewFileandFolders open={open} />}
    //                   />
    //                   <Route
    //                     exact
    //                     path="/applicationsetting"
    //                     element={<ApplicationSetting />}
    //                   />
    //                   <Route
    //                     exact
    //                     path="/viewitemunderaudit"
    //                     element={<ViewitemsUnderAudit open={open} />}
    //                   />
    //                   <Route
    //                     exact
    //                     path="/viewitemunderaudit/viewitemunderauditset/:auditSetId"
    //                     element={<ViewItemUnderAuditSet open={open} />}
    //                   />
    //                   <Route
    //                     exact
    //                     path="/viewalleventhistory"
    //                     element={<ViewAllEventHistory open={open} />}
    //                   />
    //                 </Routes>
    //               </Main>
    //             </Suspense>
    //           </>
    //         )}
    //       </Router>
    //     ) : (
    //       <Router>
    //         <Suspense fallback={<Loder />}>
    //           <Routes
    //             exact
    //             path="/scalar-box"
    //             element={<Navigate to="/scalar-box" replace />}
    //           >
    //             <Route exact path="/scalar-box" element={<LoginandSignup />} />
    //           </Routes>
    //         </Suspense>
    //       </Router>
    //     )}
    //   </>
    // );
  }
}

export default App;

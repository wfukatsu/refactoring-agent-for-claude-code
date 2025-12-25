import React, { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { styled } from "@mui/material/styles";
import Box from "@mui/material/Box";
import MuiDrawer from "@mui/material/Drawer";
import MuiAppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import List from "@mui/material/List";
import CssBaseline from "@mui/material/CssBaseline";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import { Avatar, Button } from "@mui/material";
import scalar from "../../assets/scalar.png";
import { useLocation, useNavigate } from "react-router-dom";
import TuneIcon from "@mui/icons-material/Tune";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import SettingsIcon from "@mui/icons-material/Settings";
import SupervisedUserCircleIcon from "@mui/icons-material/SupervisedUserCircle";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import Popover from "@mui/material/Popover";
import KeyboardArrowLeftIcon from "@mui/icons-material/KeyboardArrowLeft";
import KeyboardArrowRightIcon from "@mui/icons-material/KeyboardArrowRight";
import UserItemLogo from "../../assets/viewAudit";
import ViewEventLogo from "../../assets/viewEvent";

import { useTranslation } from "react-i18next";
import { BASE_URL } from "../../utils/constants";
import axios from "../../api/axios";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import { UPDATE_LANGUAGE } from "../../redux/reducerSlice/authSlice";

const drawerWidth = 309;

const openedMixin = (theme) => ({
  width: drawerWidth,
  transition: theme.transitions.create("width", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  overflowX: "hidden",
});

const closedMixin = (theme) => ({
  transition: theme.transitions.create("width", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  overflowX: "hidden",
  width: `calc(${theme.spacing(7)} + 1px)`,
  [theme.breakpoints.up("sm")]: {
    width: `calc(${theme.spacing(10)} + 1px)`,
  },
});

const DrawerHeader = styled("div")(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  justifyContent: "flex-end",
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
}));

const AppBar = styled(MuiAppBar, {
  shouldForwardProp: (prop) => prop !== "open",
})(({ theme, open }) => ({
  zIndex: theme.zIndex.drawer + 1,
  transition: theme.transitions.create(["width", "margin"], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  backgroundColor: "#000d2d",
  ...(open && {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(["width", "margin"], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  }),
}));

const Drawer = styled(MuiDrawer, {
  shouldForwardProp: (prop) => prop !== "open",
})(({ theme, open }) => ({
  width: drawerWidth,
  flexShrink: 0,
  whiteSpace: "nowrap",
  boxSizing: "border-box",
  ...(open && {
    ...openedMixin(theme),
    "& .MuiDrawer-paper": openedMixin(theme),
  }),
  ...(!open && {
    ...closedMixin(theme),
    "& .MuiDrawer-paper": closedMixin(theme),
  }),
}));

// export default function MainLayout({ open, setOpen, handleLogout }) {
//   // const [open, setOpen] = React.useState(true);
//   const location = useLocation();
//   const dispatch = useDispatch();
//   const user = useSelector((state) => state.auth.user);

//   const axiosPrivate = useAxiosPrivate();
//   const navigate = useNavigate();

//   // const [anchorEl, setAnchorEl] = useState(null);

//   // const handleClick = (event) => {
//   //   setAnchorEl(event.currentTarget);
//   // };

//   // const handleClose = () => {
//   //   setAnchorEl(null);
//   // };

//   const { t, i18n } = useTranslation();

//   function changeLanguage(e) {
//     // i18n.changeLanguage(e.target.value);
//     updateLanguageForUser(e.target.value);
//   }

//   async function updateLanguageForUser(newLanguageCode) {
//     const url = `${BASE_URL}/box/user/updateLanguageForUser?lang=${newLanguageCode}`;

//     axiosPrivate
//       .get(url)
//       .then((response) => {
//         i18n.changeLanguage(newLanguageCode);
//         dispatch(UPDATE_LANGUAGE(newLanguageCode));

//         // console.log("Response:", response.data.message);
//       })
//       .catch((error) => {
//         console.error("Error updating language:", error);
//       });
//   }

//   const [getLanguages, setLanguages] = useState([]);

//   useEffect(() => {
//     async function getAllLanguages() {
//       const url = `${BASE_URL}/box/user/getAllLanguagesSupported`;

//       axiosPrivate
//         .get(url)
//         .then((response) => {
//           setLanguages(response.data.data);
//         })
//         .catch((error) => {
//           console.log("I got the below error");
//           console.error(error);
//           setLanguages([]);
//         });
//     }

//     // getAllLanguages();
//     // i18n.changeLanguage(user.languageCode);
//   }, []);

//   const allSupportedLangues = useRef([
//     {
//       language: "English",
//       code: "en",
//     },
//     {
//       language: "Japanese",
//       code: "ja",
//     },
//   ]);

//   const id = "simple-popover";
//   const routes = [
//     {
//       name: t("manageUserRoleScreenTitleText"),
//       url: "/userrole",
//       icon: <SupervisedUserCircleIcon />,
//     },
//     {
//       name: t("manageExAuditorsandGroupSideMenuText"),
//       url: "/auditorsandgroups",
//       icon: <ManageAccountsIcon />,
//     },
//     {
//       name: t("manageAuditSetScreenSideMenuText"),
//       url: "/auditset",
//       icon: <TuneIcon />,
//     },
//     // {
//     //   name: "Application Setting",
//     //   url: "/applicationsetting",
//     //   icon: <SettingsIcon />,
//     // },
//     {
//       name: t("viewItemsUnderAuditScreenSideMenuText"),
//       url: "/viewitemunderaudit",
//       icon: <UserItemLogo />,
//     },
//     {
//       name: t("viewAllEventHistoryScreenSideMenuText"),
//       url: "/viewalleventhistory",
//       icon: <ViewEventLogo />,
//     },
//   ];

//   const genralroutes = [
//     {
//       name: t("viewItemsUnderAuditScreenSideMenuText"),
//       url: "/viewitemunderaudit",
//       icon: <UserItemLogo />,
//     },
//   ];

//   const audit_admin = [
//     {
//       name: t("manageUserRoleScreenTitleText"),
//       url: "/userrole",
//       icon: <SupervisedUserCircleIcon />,
//     },
//     {
//       name: t("manageExAuditorsandGroupSideMenuText"),
//       url: "/auditorsandgroups",
//       icon: <ManageAccountsIcon />,
//     },
//     {
//       name: t("manageAuditSetScreenSideMenuText"),
//       url: "/auditset",
//       icon: <TuneIcon />,
//     },
//     // {
//     //   name: "Application Setting",
//     //   url: "/applicationsetting",
//     //   icon: <SettingsIcon />,
//     // },
//     {
//       name: t("viewAllEventHistoryScreenSideMenuText"),
//       url: "/viewalleventhistory",
//       icon: <ViewEventLogo />,
//     },
//   ];

//   const externalroutes = [
//     {
//       name: "External User  ",
//       url: "/external",
//       icon: <SupervisedUserCircleIcon />,
//     },
//   ];

//   const handleDrawerOpen = () => {
//     setOpen(true);
//   };

//   const handleDrawerClose = () => {
//     setOpen(false);
//   };

//   return (
//     <Box sx={{ display: "flex" }}>
//       <CssBaseline />
//       <AppBar position="fixed" open={open}>
//         <Toolbar className="flex justify-between">
//           <div className="flex justify-between gap-2">
//             <IconButton
//               color="inherit"
//               aria-label="open drawer"
//               onClick={handleDrawerOpen}
//               edge="start"
//               sx={{
//                 marginRight: 5,
//                 ...(open && { display: "none" }),
//               }}
//             >
//               <img src={scalar} alt="scalar" className="w-8" />
//               <KeyboardArrowRightIcon />
//             </IconButton>
//           </div>
//           <div className="flex items-center justify-center gap-15">
//             {user && (
//               <div className="flex flex-1 gap-3 items-center">
//                 <Avatar alt="Remy Sharp">
//                   {user.name && user.name.charAt(0)}
//                 </Avatar>
//                 <div className="flex flex-col">
//                   <Typography variant="h7" noWrap component="div">
//                     {user.name}
//                   </Typography>
//                   <p className="text-sm">{user.userEmail}</p>
//                 </div>
//                 <div className="flex items-center justify-center mr-4">
//                   <select
//                     className="p-1" // You can adjust this class to include text color if necessary
//                     value={i18n.language}
//                     onChange={changeLanguage}
//                     style={{ color: "black" }} // Set text color explicitly
//                   >
//                     {allSupportedLangues.current.map((lang) => (
//                       <option key={lang.code} value={lang.code}>
//                         {lang.language}
//                       </option>
//                     ))}
//                   </select>

//                   {/* <Button
//                 aria-describedby={id}
//                 onClick={handleClick}
//                 sx={{ color: "white" }}
//               >
//                 <ArrowDropDownIcon />
//               </Button> */}
//                 </div>
//                 {/* <div className="flex items-center justify-center">

//                    <Button
//                     aria-describedby={id}
//                     onClick={handleClick}
//                     sx={{ color: "white" }}
//                   >
//                      <ArrowDropDownIcon />
//                   </Button>
//                 </div> */}
//               </div>
//             )}

//             <button
//               className="bg-[#2834d8] hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full"
//               onClick={() => {
//                 handleLogout();
//                 navigate("/", { replace: true });
//               }}
//             >
//               {t("logoutText")}
//             </button>
//           </div>
//         </Toolbar>
//       </AppBar>
//       <Drawer
//         sx={{
//           "& .MuiPaper-root": {
//             bgcolor: "#000d2d",
//             color: "white",
//           },
//         }}
//         variant="permanent"
//         open={open}
//       >
//         <div className="flex justify-center items-center h-[64px] w-full">
//           <div className="flex justify-center items-center flex-grow">
//             <img src={scalar} alt="scalar" className="w-10" />{" "}
//           </div>
//           <div>
//             <IconButton onClick={handleDrawerClose}>
//               <KeyboardArrowLeftIcon sx={{ color: "white" }} />
//             </IconButton>
//           </div>
//         </div>
//         <Divider />
//         <List>
//           {user.userRoles.length === 1 &&
//           user.userRoles[0] === "GENERAL_USER" ? (
//             <>
//               {genralroutes.map((text, index) => (
//                 <ListItem
//                   key={index}
//                   disablePadding
//                   sx={{
//                     display: "block",
//                     pr: "10px",
//                     "& .MuiButtonBase-root": location.pathname.includes(
//                       text.url
//                     )
//                       ? {
//                           bgcolor: "#0061D5",
//                           padding: "14px",
//                           borderRadius: "0px 30px 30px 0px",
//                         }
//                       : null,
//                   }}
//                   onClick={() => {
//                     navigate(text.url);
//                   }}
//                 >
//                   <ListItemButton
//                     sx={{
//                       minHeight: 48,
//                       justifyContent: open ? "initial" : "center",
//                       px: 2.5,
//                     }}
//                   >
//                     <ListItemIcon
//                       sx={{
//                         minWidth: 0,
//                         mr: open ? 1 : "auto",
//                         justifyContent: "center",
//                         color: "white",
//                       }}
//                     >
//                       {text.icon}
//                     </ListItemIcon>
//                     <ListItemText
//                       primary={text.name}
//                       sx={{ opacity: open ? 1 : 0 }}
//                     />
//                   </ListItemButton>
//                 </ListItem>
//               ))}
//             </>
//           ) : user.userRoles.length === 1 &&
//             user.userRoles[0] === "EXTERNAL_AUDITOR" ? (
//             <>
//               {externalroutes.map((text, index) => (
//                 <ListItem
//                   key={index}
//                   disablePadding
//                   sx={{
//                     display: "block",
//                     pr: "10px",
//                     "& .MuiButtonBase-root": location.pathname.includes(
//                       text.url
//                     )
//                       ? {
//                           bgcolor: "#0061D5",
//                           padding: "14px",
//                           borderRadius: "0px 30px 30px 0px",
//                         }
//                       : null,
//                   }}
//                   onClick={() => {
//                     navigate(text.url);
//                   }}
//                 >
//                   <ListItemButton
//                     sx={{
//                       minHeight: 48,
//                       justifyContent: open ? "initial" : "center",
//                       px: 2.5,
//                     }}
//                   >
//                     <ListItemIcon
//                       sx={{
//                         minWidth: 0,
//                         mr: open ? 1 : "auto",
//                         justifyContent: "center",
//                         color: "white",
//                       }}
//                     >
//                       {text.icon}
//                     </ListItemIcon>
//                     <ListItemText
//                       primary={text.name}
//                       sx={{ opacity: open ? 1 : 0 }}
//                     />
//                   </ListItemButton>
//                 </ListItem>
//               ))}
//             </>
//           ) : user.userRoles.length === 1 &&
//             user.userRoles[0] === "AUDIT_ADMIN" ? (
//             <>
//               {audit_admin.map((text, index) => (
//                 <ListItem
//                   key={index}
//                   disablePadding
//                   sx={{
//                     display: "block",
//                     pr: "10px",
//                     "& .MuiButtonBase-root": location.pathname.includes(
//                       text.url
//                     )
//                       ? {
//                           bgcolor: "#0061D5",
//                           padding: "14px",
//                           borderRadius: "0px 30px 30px 0px",
//                         }
//                       : null,
//                   }}
//                   onClick={() => {
//                     navigate(text.url);
//                   }}
//                 >
//                   <ListItemButton
//                     sx={{
//                       minHeight: 48,
//                       justifyContent: open ? "initial" : "center",
//                       px: 2.5,
//                     }}
//                   >
//                     <ListItemIcon
//                       sx={{
//                         minWidth: 0,
//                         mr: open ? 1 : "auto",
//                         justifyContent: "center",
//                         color: "white",
//                       }}
//                     >
//                       {text.icon}
//                     </ListItemIcon>
//                     <ListItemText
//                       primary={text.name}
//                       sx={{ opacity: open ? 1 : 0 }}
//                     />
//                   </ListItemButton>
//                 </ListItem>
//               ))}
//             </>
//           ) : (
//             <>
//               {routes.map((text, index) => (
//                 <ListItem
//                   key={index}
//                   disablePadding
//                   sx={{
//                     display: "block",
//                     pr: "10px",
//                     "& .MuiButtonBase-root": location.pathname.includes(
//                       text.url
//                     )
//                       ? {
//                           bgcolor: "#0061D5",
//                           padding: "14px",
//                           borderRadius: "0px 30px 30px 0px",
//                         }
//                       : null,
//                   }}
//                   onClick={() => {
//                     navigate(text.url);
//                   }}
//                 >
//                   <ListItemButton
//                     sx={{
//                       minHeight: 48,
//                       justifyContent: open ? "initial" : "center",
//                       px: 2.5,
//                     }}
//                   >
//                     <ListItemIcon
//                       sx={{
//                         minWidth: 0,
//                         mr: open ? 1 : "auto",
//                         justifyContent: "center",
//                         color: "white",
//                       }}
//                     >
//                       {text.icon}
//                     </ListItemIcon>
//                     <ListItemText
//                       primary={text.name}
//                       sx={{ opacity: open ? 1 : 0 }}
//                     />
//                   </ListItemButton>
//                 </ListItem>
//               ))}
//             </>
//           )}
//         </List>
//         <Divider />
//       </Drawer>

//       <DrawerHeader />
//     </Box>
//   );
// }

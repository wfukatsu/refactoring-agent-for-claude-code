import { useState } from "react";
import { NavLink } from "react-router-dom";
import ViewEventLogo from "../../assets/viewEvent";
import React from "react";
import "./Sidemenu.css";
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

import { useTranslation } from "react-i18next";
import { BASE_URL } from "../../utils/constants";
import axios from "../../api/axios";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import { UPDATE_LANGUAGE } from "../../redux/reducerSlice/authSlice";

const Sidemenu = ({ children }) => {
  const [open, setIsOpen] = useState(false);
  const { t, i18n } = useTranslation();
  const location = useLocation();
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);

  const axiosPrivate = useAxiosPrivate();
  const navigate = useNavigate();

  const toggle = () => setIsOpen(!open);

  const handleDrawerOpen = () => {
    // setIsOpen(true);
    toggle();
  };

  const handleDrawerClose = () => {
    // setIsOpen(false);
    toggle();
  };

  const menuItem = [
    // {
    //     path:"/",
    //     name:"Dashboard",
    //     icon:<FaTh/>
    // },
    // {
    //     path:"/about",
    //     name:"About",
    //     icon:<FaUserAlt/>
    // },
    // {
    //     path:"/analytics",
    //     name:"Analytics",
    //     icon:<FaRegChartBar/>
    // },
    // {
    //     path:"/comment",
    //     name:"Comment",
    //     icon:<FaCommentAlt/>
    // },
    // {
    //     path:"/product",
    //     name:"Product",
    //     icon:<FaShoppingBag/>
    // },
    // {
    //     path:"/productList",
    //     name:"Product List",
    //     icon:<FaThList/>
    // }
  ];

  const routes = [
    {
      name: t("manageUserRoleScreenTitleText"),
      url: "/userrole",
      icon: <SupervisedUserCircleIcon />,
    },
    {
      name: t("manageExAuditorsandGroupSideMenuText"),
      url: "/auditorsandgroups",
      icon: <ManageAccountsIcon />,
    },
    {
      name: t("manageAuditSetScreenSideMenuText"),
      url: "/auditset",
      icon: <TuneIcon />,
    },
    // {
    //   name: "Application Setting",
    //   url: "/applicationsetting",
    //   icon: <SettingsIcon />,
    // },
    {
      name: t("viewItemsUnderAuditScreenSideMenuText"),
      url: "/viewitemunderaudit",
      icon: <UserItemLogo />,
    },
    {
      name: t("viewAllEventHistoryScreenSideMenuText"),
      url: "/viewalleventhistory",
      icon: <ViewEventLogo />,
    },
  ];

  const genralroutes = [
    {
      name: t("viewItemsUnderAuditScreenSideMenuText"),
      url: "/viewitemunderaudit",
      icon: <UserItemLogo />,
    },
  ];

  const audit_admin = [
    {
      name: t("manageUserRoleScreenTitleText"),
      url: "/userrole",
      icon: <SupervisedUserCircleIcon />,
    },
    {
      name: t("manageExAuditorsandGroupSideMenuText"),
      url: "/auditorsandgroups",
      icon: <ManageAccountsIcon />,
    },
    {
      name: t("manageAuditSetScreenSideMenuText"),
      url: "/auditset",
      icon: <TuneIcon />,
    },
    // {
    //   name: "Application Setting",
    //   url: "/applicationsetting",
    //   icon: <SettingsIcon />,
    // },
    {
      name: t("viewAllEventHistoryScreenSideMenuText"),
      url: "/viewalleventhistory",
      icon: <ViewEventLogo />,
    },
  ];

  const externalroutes = [
    {
      name: "External User  ",
      url: "/external",
      icon: <SupervisedUserCircleIcon />,
    },
  ];

  return (
    <div className="flex">
      <div className="sidebar">
        <div className="top_section" style={{ width: open ? "310px" : "70px" }}>
          {open && (
            <div className="flex justify-center items-center flex-grow">
              <img src={scalar} alt="scalar" className="w-10" />
            </div>
          )}

          {!open ? (
            //   <div className="flex justify-between gap-2">
            <IconButton
              color="inherit"
              aria-label="open drawer"
              onClick={handleDrawerOpen}
              edge="start"
              // sx={{
              //   paddingLeft: 2,
              //   //   ...(open && { display: "none" }),
              // }}
            >
              <img src={scalar} alt="scalar" className="w-8" />
              <KeyboardArrowRightIcon />
            </IconButton>
          ) : (
            //   {/* </div> */}
            <IconButton onClick={handleDrawerClose}>
              <KeyboardArrowLeftIcon sx={{ color: "white" }} />
            </IconButton>
          )}
        </div>
        <Divider />
        <List>
          {user.userRoles.length === 1 &&
          user.userRoles[0] === "GENERAL_USER" ? (
            <>
              {genralroutes.map((text, index) => (
                <ListItem
                  key={index}
                  disablePadding
                  sx={{
                    display: "block",
                    pr: "10px",
                    "& .MuiButtonBase-root": location.pathname.includes(
                      text.url
                    )
                      ? {
                          bgcolor: "#0061D5",
                          padding: "14px",
                          borderRadius: "0px 30px 30px 0px",
                        }
                      : null,
                  }}
                  onClick={() => {
                    navigate(text.url);
                  }}
                >
                  <ListItemButton
                    sx={{
                      minHeight: 48,
                      justifyContent: open ? "initial" : "center",
                      px: 2.5,
                      mb: 1,
                    }}
                  >
                    <ListItemIcon
                      sx={{
                        minWidth: 0,
                        mr: open ? 1 : "auto",
                        justifyContent: "center",
                        color: "white",
                      }}
                    >
                      {text.icon}
                    </ListItemIcon>
                    {open && (
                      <ListItemText
                        primary={text.name}
                        sx={{ opacity: open ? 1 : 0 }}
                      />
                    )}
                  </ListItemButton>
                </ListItem>
              ))}
            </>
          ) : user.userRoles.length === 1 &&
            user.userRoles[0] === "EXTERNAL_AUDITOR" ? (
            <>
              {externalroutes.map((text, index) => (
                <ListItem
                  key={index}
                  disablePadding
                  sx={{
                    display: "block",
                    pr: "10px",
                    "& .MuiButtonBase-root": location.pathname.includes(
                      text.url
                    )
                      ? {
                          bgcolor: "#0061D5",
                          padding: "14px",
                          borderRadius: "0px 30px 30px 0px",
                        }
                      : null,
                  }}
                  onClick={() => {
                    navigate(text.url);
                  }}
                >
                  <ListItemButton
                    sx={{
                      minHeight: 48,
                      justifyContent: open ? "initial" : "center",
                      px: 2.5,
                    }}
                  >
                    <ListItemIcon
                      sx={{
                        minWidth: 0,
                        mr: open ? 1 : "auto",
                        justifyContent: "center",
                        color: "white",
                      }}
                    >
                      {text.icon}
                    </ListItemIcon>
                    <ListItemText
                      primary={text.name}
                      sx={{ opacity: open ? 1 : 0 }}
                    />
                  </ListItemButton>
                </ListItem>
              ))}
            </>
          ) : user.userRoles.length === 1 &&
            user.userRoles[0] === "AUDIT_ADMIN" ? (
            <>
              {audit_admin.map((text, index) => (
                <ListItem
                  key={index}
                  disablePadding
                  sx={{
                    display: "block",
                    pr: "5px",
                    "& .MuiButtonBase-root": location.pathname.includes(
                      text.url
                    )
                      ? {
                          bgcolor: "#0061D5",
                          padding: "14px",
                          borderRadius: "0px 30px 30px 0px",
                        }
                      : null,
                  }}
                  onClick={() => {
                    navigate(text.url);
                  }}
                >
                  <ListItemButton
                    sx={{
                      minHeight: 48,
                      justifyContent: open ? "initial" : "center",
                      px: 2.5,
                      mb: 1,
                    }}
                  >
                    <ListItemIcon
                      sx={{
                        minWidth: 0,
                        mr: open ? 1 : "auto",
                        justifyContent: "center",
                        color: "white",
                      }}
                    >
                      {text.icon}
                    </ListItemIcon>
                    {open && (
                      <ListItemText
                        primary={text.name}
                        sx={{ opacity: open ? 1 : 0 }}
                      />
                    )}
                  </ListItemButton>
                </ListItem>
              ))}
            </>
          ) : (
            <>
              {routes.map((text, index) => (
                <ListItem
                  key={index}
                  disablePadding
                  sx={{
                    display: "block",
                    pr: "5px",

                    "& .MuiButtonBase-root": location.pathname.includes(
                      text.url
                    )
                      ? {
                          bgcolor: "#0061D5",
                          padding: "14px",
                          borderRadius: "0px 30px 30px 0px",
                        }
                      : null,
                  }}
                  onClick={() => {
                    navigate(text.url);
                  }}
                >
                  <ListItemButton
                    sx={{
                      minHeight: 48,
                      justifyContent: open ? "initial" : "center",
                      px: 2.5,
                      mb: 1,
                    }}
                  >
                    <ListItemIcon
                      sx={{
                        minWidth: 0,
                        mr: open ? 1 : "auto",
                        justifyContent: "center",
                        color: "white",
                      }}
                    >
                      {text.icon}
                    </ListItemIcon>

                    {open && (
                      <ListItemText
                        primary={text.name}
                        sx={{ opacity: open ? 1 : 0 }}
                      />
                    )}
                  </ListItemButton>
                </ListItem>
              ))}
            </>
          )}
        </List>
        <Divider />
        {/* {menuItem.map((item, index) => (
          <NavLink
            to={item.path}
            key={index}
            className="link"
            activeclassName="active"
          >
            <div className="icon">{item.icon}</div>
            <div
              style={{ display: open ? "block" : "none" }}
              className="link_text"
            >
              {item.name}
            </div>
          </NavLink>
        ))} */}
      </div>
      <main>{children}</main>
    </div>
  );
};

export default Sidemenu;

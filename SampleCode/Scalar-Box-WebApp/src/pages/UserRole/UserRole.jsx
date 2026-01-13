import React, { useEffect, useState } from "react";
import "./UserRole.css";
import { Checkbox, TextField, Tooltip } from "@mui/material";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import { styled } from "@mui/material/styles";
import { useSelector } from "react-redux";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import { useTheme } from "@mui/material/styles";
import Box from "@mui/material/Box";
import OutlinedInput from "@mui/material/OutlinedInput";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import SuccessPopUp from "../../common/SuccessPopUp/SuccessPopUp";
import ErrorPopUp from "../../common/ErrorPopUp/ErrorPopUp";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import PlusOneIcon from "@mui/icons-material/PlusOne";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

import MuiCircularProgress from "@mui/joy/CircularProgress";

import Chip from "@mui/material/Chip";
import { BASE_URL } from "../../utils/constants";
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

const names = ["GENERAL_USER", "AUDIT_ADMIN"];

function getStyles(name, personName, theme) {
  return {
    fontWeight:
      personName.indexOf(name) === -1
        ? theme.typography.fontWeightRegular
        : theme.typography.fontWeightMedium,
  };
}

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#0061D5",
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));



const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  "& .css-1t1j96h-MuiPaper-root-MuiDialog-paper": {
    padding: theme.spacing(1),
    borderRadius: "20px",
  },
  "& .MuiDialogContent-root": {
    padding: theme.spacing(3),
  },
  "& .MuiDialogActions-root": {
    padding: theme.spacing(1),
  },
}));

const UserRole = ({ open, main }) => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const { orgId } = useSelector((state) => state.auth.user);
  const [userInfo, setUserInfo] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedRow, setSelectedRow] = useState(null);
  const [selectedRowData, setSelectedRowData] = useState(null);
  const [openDilogBox, setOpenDilogBox] = useState(false);
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [showErrorPopup, setShowErrorPopup] = useState(false);
  const [message, setMessage] = useState("");
  const theme = useTheme();
  const [personName, setPersonName] = useState([]);
  const [showRoles, setShowRoles] = useState({});
  const axiosPrivate = useAxiosPrivate();

  const [loadingTwo, setLoadingTwo] = useState(false);

  const {t,i18n} = useTranslation();

  const columns = [
    { id: "select", label: t("manageUserRoleScreenSelectText"), minWidth: 50, align: "left" },
    { id: "userEmail", label: t("manageUserRoleScreenEmailText"), minWidth: 150, align: "left" },
    { id: "name", label: t("manageUserRoleScreenNameText"), minWidth: 150, align: "left" },
    { id: "roleJson", label: t("manageUserRoleScreenRolesText"), minWidth: 150, align: "left" },
  ];

  const drawerWidth = 309;
  const Main = styled("main")(({ theme }) => ({
    marginLeft: 82,
    flexGrow: 1,
    padding: theme.spacing(2),
    paddingTop: 15,
    backgroundColor: "#ffff",
    transition: theme.transitions.create("margin", {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    ...(open && {
      marginLeft: `${drawerWidth}px`,
      transition: theme.transitions.create("margin", {
        easing: theme.transitions.easing.easeOut,
        duration: theme.transitions.duration.enteringScreen,
      }),
    }),
  }));

  const toggleShowRoles = (userEmail) => {
    setShowRoles((prev) => ({
      ...prev,
      [userEmail]: !prev[userEmail],
    }));
  };

  const handleChange = (event, userEmail) => {
    const {
      target: { value },
    } = event;
    setPersonName(typeof value === "string" ? value.split(",") : value);

    setSelectedRowData((prevData) => ({
      ...prevData,
      roleJson: value,
    }));
  };

  const handleClickOpen = (rowData) => {
    setSelectedRowData(rowData);
    setOpenDilogBox(true);
    setPersonName(rowData.roleJson);
    // }
  };
  const handleClose = () => {
    setOpenDilogBox(false);
  };

  const fetchUserInfo = async () => {
    try {
      const response = await axiosPrivate.get(
        `${BASE_URL}/box/user/getManagedUsers/${orgId}`
      );
      if (response.status === 200) {
        setUserInfo(response.data.data || []);

        setLoadingTwo(true);
      } else {
        setError(`Failed to fetch user data: ${response.statusText}`);
        console.error("Failed to fetch user data:", response.statusText);
      }
    } catch (error) {
      console.error("Error fetching user data:", error);
      setError("Error fetching user data");
      setShowErrorPopup(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    console.log("11111111111111 useEffect UserRole");
    fetchUserInfo();
  }, []);

  // const handleCheckboxChange = (event, userId) => {
  //   if (selectedRow === userId) {
  //     setSelectedRow(null);
  //   } else {
  //     setSelectedRow(userId);
  //   }
  // };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const handleApplyRoleChanges = async () => {
    try {
      const response = await axiosPrivate.put(
        `${BASE_URL}/box/user/updateUserRole/${selectedRowData.userEmail}`,
        { newRoles: selectedRowData.roleJson }
      );

      if (response.status === 200) {
        setShowSuccessPopup(true);
        fetchUserInfo();
        setMessage(response.data.message);
        setSelectedRow(null); // Uncheck the selected row checkbox
        setPersonName([]); // Clear the selected roles
      } else {
        console.log(error);
      }
    } catch (error) {
      setMessage(error.response.data.message);
      setShowErrorPopup(true);
    } finally {
      handleClose();
    }
  };

  const handleRoleRemove = (indexToRemove) => {
    setSelectedRowData((prevData) => ({
      ...prevData,
      roleJson: prevData.roleJson.filter((_, index) => index !== indexToRemove),
    }));
    setPersonName((prevPersonName) =>
      prevPersonName.filter((_, index) => index !== indexToRemove)
    );
  };

  console.log(selectedRow, "selectedRow");

  return (
    // <Main open={open}>
    <div className="flex flex-col gap-2">
      <div className="flex justify-between">
        <h1 className="font-bold text-xl">{t("manageUserRoleScreenTitleText")}</h1>
        <button
          className={`bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
            selectedRow === null ? "opacity-50 cursor-not-allowed" : ""
          }`}
          disabled={selectedRow === null}
          onClick={() => {
            if (selectedRow !== null) {
              handleClickOpen(selectedRow);
            }
          }}
        >
          {t("manageUserRoleScreenButtonText")}
        </button>
      </div>
      <div>
        {loadingTwo ? (
          <Paper
            style={{ width: "100%", overflow: "hidden", borderRadius: "25px" }}
          >
            {loading && <p>Loading...</p>}
            {error && <p>{error}</p>}
            {!loading && !error && (
              <>
                <TableContainer style={{ maxHeight: 440 }}>
                  <Table aria-label="sticky table">
                    <TableHead>
                      <TableRow>
                        {columns.map((column) => (
                          <StyledTableCell
                            key={column.id}
                            align={column.align || "left"}
                            style={{
                              width: column.minWidth,
                              paddingRight: column.id === "select" ? "20px" : 0,
                            }}
                          >
                            {column.label}
                          </StyledTableCell>
                        ))}
                      </TableRow>
                    </TableHead>
                    {userInfo && userInfo.length === 0 ? (
                      <TableBody>
                        <TableRow>
                          <TableCell colSpan={5}>
                            <div
                              style={{
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                                minHeight: "400px",
                              }}
                            >
                              <p className="text-xl font-bold">
                                {t("manageUserRoleScreenEmptyListText")}
                              </p>
                            </div>
                          </TableCell>
                        </TableRow>
                      </TableBody>
                    ) : (
                      <TableBody>
                        {userInfo
                          .slice(
                            page * rowsPerPage,
                            page * rowsPerPage + rowsPerPage
                          )
                          .map((row, index) => (
                            <TableRow
                              hover
                              role="checkbox"
                              tabIndex={-1}
                              key={index}
                            >
                              <TableCell align="left" style={{ width: "5%" }}>
                                <Checkbox
                                  icon={<RadioButtonUncheckedIcon />}
                                  checkedIcon={<CheckCircleIcon />}
                                  checked={
                                    selectedRow?.userEmail === row.userEmail
                                  }
                                  onChange={() => {
                                    if (
                                      selectedRow &&
                                      selectedRow.userEmail === row.userEmail
                                    ) {
                                      setSelectedRow(null); // Deselect the row if it was already selected
                                    } else {
                                      setSelectedRow(row); // Select the row
                                    }
                                  }}
                                  size="small"
                                />
                              </TableCell>
                              {columns.slice(1).map((column) => (
                                <TableCell key={column.id} align="left">
                                  {column.id === "roleJson" ? (
                                    <div>
                                      {showRoles[row.userEmail] ? (
                                        <ul>
                                          {row.roleJson.map(
                                            (role, roleIndex) => (
                                              <li key={roleIndex}>{role}</li>
                                            )
                                          )}
                                        </ul>
                                      ) : (
                                        <>
                                          {row.roleJson.length > 1 ? (
                                            <div className="flex gap-1 items-center">
                                              <div>{row.roleJson[0]}</div>
                                              <Tooltip
                                                title={row.roleJson
                                                  .slice(1)
                                                  .join(", ")}
                                                placement="right"
                                              >
                                                <div className="bg-blue-500 p-1 border rounded-3xl">
                                                  <PlusOneIcon
                                                    fontSize="small"
                                                    style={{ color: "white" }}
                                                  />
                                                </div>
                                              </Tooltip>
                                            </div>
                                          ) : (
                                            row.roleJson[0]
                                          )}
                                        </>
                                      )}
                                    </div>
                                  ) : (
                                    row[column.id]
                                  )}
                                </TableCell>
                              ))}
                            </TableRow>
                          ))}
                      </TableBody>
                    )}
                  </Table>
                </TableContainer>
                <TablePagination
                  rowsPerPageOptions={[10, 25, 100]}
                  component="div"
                  count={userInfo.length}
                  rowsPerPage={rowsPerPage}
                  labelRowsPerPage={t("tablePaginationTitleText")}
                  page={page}
                  onPageChange={handleChangePage}
                  onRowsPerPageChange={handleChangeRowsPerPage}
                  labelDisplayedRows={({ from, to, count }) => {
                    if(i18n.language == "ja"){
                      return `全アイテム  ${count} ${t("tablePageOffText")} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(count / rowsPerPage)})`;
                    }

                    return `${from}-${to} ${t("tablePageOffText")} ${count} (${Math.ceil(
                      count / rowsPerPage
                    )} ${t("tablePageText")})`;
                  }}
                />
              </>
            )}
          </Paper>
        ) : (
          <div
            style={{
              height: "50vh",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <MuiCircularProgress />
          </div>
        )}
      </div>
      {showSuccessPopup && (
        <SuccessPopUp
          iconColor="primary"
          iconSize={50}
          title={message}
          open={showSuccessPopup}
          handleClose={() => setShowSuccessPopup(false)}
        />
      )}
      {showErrorPopup && (
        <ErrorPopUp
          iconColor="error"
          iconSize={50}
          title={message}
          open={showErrorPopup}
          handleClose={() => {
            setShowErrorPopup(false);
            setMessage("");
          }}
        />
      )}
      <BootstrapDialog
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        open={openDilogBox}
      >
        <DialogTitle
          sx={{ m: 0, p: 2, fontSize: "25px", fontWeight: "bold" }}
          id="customized-dialog-title"
        >
          {t("manageUserRoleScreenDailogTitleText")}
        </DialogTitle>
        <IconButton
          aria-label="close"
          onClick={handleClose}
          sx={{
            position: "absolute",
            right: 8,
            top: 8,
            color: (theme) => theme.palette.grey[500],
          }}
        >
          <CloseIcon />
        </IconButton>
        <DialogContent dividers>
          {selectedRowData && (
            <div className="flex flex-col gap-6">
              <div className="UserName" data-content={t("manageUserRoleScreenDailogLabelNameText")}>
                <div className="custom-lable-row">
                  <TextField
                    sx={{ paddingLeft: "10px", paddingRight: "10px" }}
                    variant="standard"
                    value={selectedRowData.name}
                    color="primary"
                    InputProps={{ disableUnderline: true }}
                    disabled
                  />
                </div>
              </div>

              <div className="flex flex-col gap-2">
                {/* <FormControl sx={{ width: 340 }}> */}
                {/* <InputLabel id="demo-multiple-chip-label">
                    {personName.length === 0 ? "Select " : " Role"}
                  </InputLabel> */}
                <div className="UserRolePage" data-content={t("manageUserRoleScreenDailogLabelRoleText")}>
                  <div className="custom-lable-row">
                    <Select
                      sx={{
                        width: "300px",
                        boxShadow: "none",
                        ".MuiOutlinedInput-notchedOutline": { border: 0 },
                        "&.MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline":
                          {
                            border: 0,
                          },
                        "&.MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline":
                          {
                            border: 0,
                          },
                      }}
                      displayEmpty
                      multiple
                      value={personName}
                      onChange={(event) => handleChange(event, selectedRow)}
                      input={
                        <OutlinedInput id="select-multiple-chip" label="Chip" />
                      }
                      renderValue={(selected) => {
                        if (selected.length === 0) {
                          return <p>{t("manageUserRoleScreenDailogSelectDropdownText")}</p>;
                        }
                        return <p>{t("manageUserRoleScreenDailogSelectDropdownText")}</p>;
                      }}
                      MenuProps={MenuProps}
                    >
                      {names.map((name) => (
                        <MenuItem
                          key={name}
                          value={name}
                          style={getStyles(name, personName, theme)}
                        >
                          <Box
                            sx={{
                              display: "flex",
                              justifyContent: "space-between",
                              alignItems: "center",
                            }}
                          >
                            <Checkbox checked={personName.indexOf(name) > -1} />
                            <span>{name}</span>{" "}
                          </Box>
                        </MenuItem>
                      ))}
                    </Select>
                  </div>
                </div>
                {/* </FormControl> */}

                <div className="flex flex-row gap-2">
                  {selectedRowData.roleJson.map((role, index) => (
                    <div
                      key={index}
                      className="flex items-center text-[11px] bg-[#d6e4f7] h-8 text-black py-0 px-1 rounded-full "
                    >
                      {role}
                      <IconButton
                        aria-label="close"
                        sx={{
                          color: (theme) => theme.palette.grey[500],
                        }}
                        onClick={() => handleRoleRemove(index)}
                      >
                        <CloseIcon />
                      </IconButton>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </DialogContent>
        <DialogActions>
          <button
            autoFocus
            onClick={handleApplyRoleChanges}
            className={`bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full  ${
              personName.length === 0 ? "opacity-50 cursor-not-allowed" : ""
            }`}
            disabled={personName.length === 0}
          >
            {t("manageUserRoleScreenDailogApplyButtonText")}
          </button>
        </DialogActions>
      </BootstrapDialog>
    </div>
    // {/* </Main> */}
  );
};

export default UserRole;

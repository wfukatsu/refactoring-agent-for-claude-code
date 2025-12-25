import * as React from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";
import FormControl from "@mui/material/FormControl";

import MenuItem from "@mui/material/MenuItem";
import { useSelector } from "react-redux";
import { useState } from "react";
import {
  Checkbox,
  Divider,
  IconButton,
  InputAdornment,
  SvgIcon,
} from "@mui/material";
import SuccessPopUp from "../../../common/SuccessPopUp/SuccessPopUp";
import ErrorPopUp from "../../../common/ErrorPopUp/ErrorPopUp";
import CloseIcon from "@mui/icons-material/Close";
import { BASE_URL } from "../../../utils/constants";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
import OutlinedInput from "@mui/material/OutlinedInput";

import { useTranslation } from "react-i18next";

import "../AuditorsAndGroups.css";

export default function AddAuditGroup({ AuditUsersList, onClick }) {
  const [open, setOpen] = useState(false);
  const [name, SetName] = useState("");
  const [info, setInfo] = useState("");
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [showErrorPopup, setShowErrorPopup] = useState(false);
  const [open2, setOpen2] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [msg, setMsg] = useState("");
  const axiosPrivate = useAxiosPrivate();

  const {t,i18n} = useTranslation();

  const removeItem = (indexToRemove) => {
    setSelectedUsers((prevItems) =>
      prevItems.filter((item, index) => index !== indexToRemove)
    );
  };

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClosePopup = async () => {
    setOpen(false);
    SetName("");
    setInfo("");
    setMessageForName("");
    setMessageForInfo("");
    setOpen2(false);
    setSelectedUsers([]);
    setIsPrevious(false);
  };

  const handleClosePopup2 = async () => {
    setOpen2(false);
    setSelectedUsers([]);
    onClick();
  };

  const handleToggle = (option) => {
    console.log("o--->", option);
    const inputBody = {
      userEmail: option.userEmail,
      userName: option.name,
    };
    const selectedIndex = selectedUsers.findIndex(
      (user) => user.userEmail === option.userEmail
    );
    let newSelectedUsers = [...selectedUsers];

    if (selectedIndex === -1) {
      newSelectedUsers.push(inputBody);
    } else {
      newSelectedUsers.splice(selectedIndex, 1);
    }
    setSelectedUsers(newSelectedUsers);
  };

  const [isPrevious, setIsPrevious] = useState(false);

  const [originalName, setOriginalName] = useState("");
  const [originalInfo, setOriginalInfo] = useState("");

  // Function to update originalName and originalInfo when name and info change
  const savePreviousData = (newName, newInfo) => {
    setOriginalName(newName);
    setOriginalInfo(newInfo);
  };

  const handleAddMembers = async () => {
    // const responsebody = {
    //     auditGroupUserList: selectedUsers,
    //     auditGroupId: createdId
    // }

    const responsebody = {
      auditGroupName: name,
      description: info,
      auditGroupUserList: selectedUsers,
    };

    console.log("Responsebody:", responsebody);

    const response = axiosPrivate.post(
      `${BASE_URL}/box/auditGroup/createAuditGroup`,
      responsebody
    );
    response.then(
      (resp) => {
        console.log("Success ", resp.data);
        setMsg(resp.data.message);
        handleClosePopup();
        onClick();
        setShowSuccessPopup(true);
      },
      (rej) => {
        console.log("Failure ", rej);
        setMsg(rej.response.data.message);
        handleClosePopup();
        setShowErrorPopup(true);
      }
    );
  };

  const svgContent2 = `
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
  <path d="M0.5 17.5V14.1154L13.6904 0.930775C13.8416 0.793425 14.0086 0.687292 14.1913 0.612375C14.374 0.537458 14.5656 0.5 14.7661 0.5C14.9666 0.5 15.1608 0.535584 15.3488 0.60675C15.5368 0.677901 15.7032 0.791034 15.848 0.94615L17.0692 2.18268C17.2243 2.32754 17.3349 2.49424 17.4009 2.68278C17.4669 2.87129 17.5 3.05981 17.5 3.24833C17.5 3.44941 17.4656 3.64131 17.3969 3.82403C17.3283 4.00676 17.219 4.17373 17.0692 4.32495L3.88458 17.5H0.5ZM14.5519 4.6942L16 3.25573L14.7442 1.99998L13.3058 3.44805L14.5519 4.6942Z" fill="#00132B"/>
  </svg>
  `;

  const [messageForName, setMessageForName] = React.useState('');

  const handleNameChange = (e) => {
      const enteredName = e.target.value;

      SetName(enteredName);

      if (enteredName.trim() === "") {
          // If the input is empty or contains only whitespace, set a helper text message.
          setMessageForName(t("manageExGroupScreenDailogErMessageOneText"));
      } else if(enteredName.length < 3 ){
          setMessageForName(t("manageExGroupScreenDailogErMessageTwoText"));
      } else {
          // If the input is valid, clear any previous helper text.
          setMessageForName('');
      }
  };

  const [messageForInfo, setMessageForInfo] = React.useState('');

  const handleNameInfoChange = (e) => {
      const enteredName = e.target.value;

      setInfo(enteredName);

      if (enteredName.trim() === "") {
          // If the input is empty or contains only whitespace, set a helper text message.
          setMessageForInfo(t("manageExGroupScreenDailogErMessageThreeText"));
      } else {
          // If the input is valid, clear any previous helper text.
          setMessageForInfo('');
      }
  };

  const [selectedValues, setSelectedValues] = useState([]);

  return (
    <React.Fragment>
        <button className=" bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full" onClick={handleClickOpen}>
        {t("manageExAuditorsScreenCreateText")}
        </button>

        <Dialog
            open={open}
            onClose={handleClosePopup}
            PaperProps={{
                component: 'form',
                onSubmit: (event) => {
                    event.preventDefault();
                    const formData = new FormData(event.currentTarget);
                    const formJson = Object.fromEntries(formData.entries());
                    const email = formJson.email;
                    console.log(email);
                },
                style: { width: 340 }
            }}

        >
            {!open2 ? <DialogTitle sx={{ fontWeight: "700", color: "black" }}>
                <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between" }}>
                    <h1>{t("manageExGroupScreenDailogTitleText")}</h1>
                    <CloseIcon onClick={handleClosePopup}></CloseIcon>
                </div>
            </DialogTitle>
                :
                <DialogTitle sx={{ fontWeight: "700", color: "black" }}>
                    <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between" }}>
                        <h1>{t("manageExGroupScreenDailogAddMembersTitleText")}</h1>
                        <CloseIcon onClick={handleClosePopup}></CloseIcon>
                    </div>
                </DialogTitle>}

            {!open2 ? 
            <DialogContent>
                <Divider />
                <br></br>

                <div>
                <div className="AuditGroupName" data-content={t("manageExGroupScreenDailogLabelGroupNameText")}>
                <div className="custom-lable-row">
                    <TextField
                    sx={{ paddingLeft: "10px", paddingRight: "10px" }}
                        // label="Enter Group Name"
                        color="primary"
                        value={name}
                        // InputLabelProps={{
                        //     style: { color: 'white', backgroundColor: '#0061D5', borderRadius: "20px", paddingLeft: "10px", paddingRight: "10px" },
                        // }}
                        inputProps={{ maxLength: 30 }}
                        onChange={handleNameChange}
                        placeholder={t("manageExGroupScreenDailogHintGroupNameText")}
                        focused
                        fullWidth
                        variant="standard"
                        InputProps={{
                            disableUnderline: true,
                          }}

                    />

                    
                    </div>
                </div>
                {messageForName && (
                        <p style={{ color: 'red', fontSize: '12px' }}>
                            {messageForName}
                        </p>
                    )}
                </div>


                <br></br>
                <div>
                <div className='AuditGroupDescription' data-content={t("manageExGroupScreenDailogLabelGroupDesText")}>
                <div className="custom-lable-row">
                    <TextField
                        id="outlined-multiline-static"
                        // label="Enter Group Info"
                        multiline
                        rows={5}
                        // InputLabelProps={{
                        //     style: { color: 'white', backgroundColor: '#0061D5', borderRadius: "20px", paddingLeft: "10px", paddingRight: "10px" },
                        // }}
                        sx={{ paddingLeft: "10px", paddingRight: "10px" }}
                        variant="standard"
                        inputProps={{ maxLength: 100 }}
                        InputProps={{
                            disableUnderline: true,
                            endAdornment: (
                                // <p style={{
                                //     fontSize: "small",
                                //     position: "absolute",
                                //     top: 0,
                                //     right: 10,
                                // }}>{info ? info.length : 0}/100</p>
                                <div
                      style={{
                        display: "flex",
                        // backgroundColor: "red",
                        height: "110px",
                        fontSize: "small",
                      }}
                    >
                      {info ? info.length : 0}/100
                    </div>
                            ),
                        }}
                        placeholder={t("manageExGroupScreenDailogHintGroupDes")}
                        fullWidth
                        value={info} onChange={handleNameInfoChange}
                        focused
                    />
                    </div>
                    </div>
                    {messageForInfo && (
                        <p style={{ color: 'red', fontSize: '12px' }}>
                            {messageForInfo}
                        </p>
                    )}
                </div>
            </DialogContent>
                :
                <DialogContent>
                    <Divider />
                    <br></br>

                    <div className="AuditGroupMemebers" data-content={t("manageExGroupScreenDailogUpdateLabelUsernameText")}>
                        <div className="custom-lable-row">
                            <Select
                                sx={{
                                    width: "300px",
                                    boxShadow: "none",
                                    ".MuiOutlinedInput-notchedOutline": { border: 0 },
                                    "&.MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline": {
                                        border: 0,
                                    },
                                    "&.MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline":
                                    {
                                        border: 0,
                                    },
                                }}
                                multiple
                                value={selectedValues}
                                onChange={(event) => setSelectedValues(event.target.value)}
                                input={<OutlinedInput id="select-multiple-chip" label="Chip" />}
                                displayEmpty
                                renderValue={(selected) => {
                                    if (selected.length === 0) {
                                        return <p>{t("manageExGroupScreenDailogUpdateHintUsernameText")}</p>;
                                    }
                                    return <p>{t("manageExGroupScreenDailogUpdateHintUsernameText")}</p>;
                                }}
                            >
                                <MenuItem value={[]}>
                                    <em>{t("manageExGroupScreenDailogUpdateHintUsernameText")}</em>
                                </MenuItem>
                                {AuditUsersList.map((option, index) => (
                                    <MenuItem key={index} value={{ userEmail: option.userEmail, userName: option.name }}>
                                        <Checkbox
                                            checked={selectedUsers.some(user => user.userEmail === option.userEmail)} // Check if the user object exists in the selectedUsers array
                                            onChange={(event) => {
                                                const isChecked = event.target.checked;
                                                if (isChecked) {
                                                    setSelectedUsers(prevSelectedUsers => [...prevSelectedUsers, { userEmail: option.userEmail, userName: option.name }]);
                                                } else {
                                                    setSelectedUsers(prevSelectedUsers => prevSelectedUsers.filter(user => user.userEmail !== option.userEmail));
                                                }
                                            }}
                                        />
                                        {option.name}
                                    </MenuItem>
                                ))}
                            </Select>

                        </div>
                    </div>

                    <div style={{ height: "2px" }}></div>
                    <div style={{ display: "flex", flexDirection: "row", flexWrap: "wrap", width: "277px" }}>
                        {selectedUsers && selectedUsers.map((e, index) => {
                            return (
                                <div key={index} className='justify-between border rounded-lg p-1 text-white' style={{ backgroundColor: "rgba(0,97,213,0.2)", borderRadius: "20px", color: "black", gap: "2px", fontSize: "12px", display: "flex", alignItems: "center", margin: "2px" }}>
                                    <span>{e.userName}</span>
                                    <span style={{ cursor: "pointer" }} onClick={() => removeItem(index)}>X</span>
                                </div>
                            );
                        })}
                    </div>

                    <div style={{ height: "10px", width: "10px" }}></div>

                </DialogContent>
            }
            {!open2 ? <DialogActions>
                <button className=" bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-1 px-4 rounded-full" onClick={()=>{
                    setOpen2(true);
                }}
                    disabled={!true || name.trim() == "" || info.trim() == "" || info.length > 100}
                    style={{ marginRight: "20px", marginBottom: "10px", paddingLeft: "20px", paddingRight: "20px", backgroundColor: (!name.trim() || !info.trim() || !(info.length < 101)) ? 'rgba(0,97,213,0.6)' : '#0061D5' }}
                >{t("manageExGroupScreenDailogNextButton")}</button>
            </DialogActions> :
                <DialogActions>
                    <Button
                        variant="outlined"
                        sx={{ borderRadius: 28, textTransform: "none" }}
                        disableRipple
                        onClick={(e) => {
                            setOpen(true);
                            setOpen2(false);

                        }}>
                        {t("manageExGroupScreenDailogUpdateMembersBackButton")} </Button>
                    <button className=" bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-1 px-4 m-2 rounded-full" onClick={handleAddMembers}>{t("manageExGroupScreenDailogAddMembersButtonText")}</button>
                </DialogActions>
            }

        </Dialog>


        {showSuccessPopup && (
            <SuccessPopUp
                iconColor="primary"
                iconSize={50}
                title={msg}
                open={showSuccessPopup}
                handleClose={() => setShowSuccessPopup(false)}
            />
        )}
        {showErrorPopup && (
            <ErrorPopUp
                iconColor="error"
                iconSize={50}
                title={msg}
                open={showErrorPopup}
                handleClose={() => setShowErrorPopup(false)}
            />
        )}
    </React.Fragment>
);
}

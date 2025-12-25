import * as React from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import MenuItem from "@mui/material/MenuItem";
import { useSelector } from "react-redux";
import { useState } from "react";
import { Divider, IconButton, InputAdornment, SvgIcon } from "@mui/material";

import SuccessPopUp from "../../../common/SuccessPopUp/SuccessPopUp";
import ErrorPopUp from "../../../common/ErrorPopUp/ErrorPopUp";
import CloseIcon from "@mui/icons-material/Close";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { useEffect } from "react";
import { BASE_URL } from "../../../utils/constants";

import Autocomplete from "@mui/material/Autocomplete";
import "../AuditorsAndGroups.css";
import GlobalStyles from "@mui/material/GlobalStyles";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

export default function AddExternalAuditor({ onClick }) {
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");
  const [email, SetEmail] = useState("");
  const [password, SetPassword] = useState("");
  const [org, SetOrg] = useState("");
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [showErrorPopup, setShowErrorPopup] = useState(false);
  const [msg, setMsg] = useState("");
  const [OrgList, setOrgList1] = useState([]);
  const axiosPrivate = useAxiosPrivate();
  const [showPassword, setShowPassword] = useState(false);

  const {t,i18n} = useTranslation();

  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  };

  const handleClickOpen = () => {
    handleGetOrgList();
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
    setName("");
    SetEmail("");
    SetPassword("");
    SetOrg([]);
    setHelperTextName("");
    setHelperTextEmail("");
    setHelperTextPass("");
  };

  const handleAdd = async () => {
    const responsebody = {
      name: name,
      userEmail: email,
      organizationName: org,
      password: password,
      role: "EXTERNAL_AUDITOR",
    };

    const response = axiosPrivate.post(
      `${BASE_URL}/box/user/createUser`,
      responsebody
    );
    response.then(
      (resp) => {
        console.log("Success ", resp.data.message);
        handleClose();
        onClick();
        setMsg(resp.data.message);
        setShowSuccessPopup(true);
      },
      (rej) => {
        console.log("Failure ", rej.response.data);
        handleClose();
        setMsg(rej.response.data.message);
        setShowErrorPopup(true);
      }
    );
    setOpen(false);
  };

  async function handleGetOrgList() {
    try {
      const response = await axiosPrivate.get(
        `${BASE_URL}/box/user/getOrgList`
      );
      console.log("------->", response.data.status);
      console.log(response.data.data);
      const orgNames = response.data.data.map((item) => item.organizationName);
      setOrgList1(orgNames);
      // setAuditGroupData(response.data.data)
    } catch (error) {
      console.log("I got the below error");
      console.error(error);
    }
  }

  //${BASE_URL}/box/user/getOrgList
  // const OrgList = [
  //   {
  //     value: 'Scalar-Box-Event-Log-Fetcher',
  //     label: 'Scalar-Box-Event-Log-Fetcher',
  //   },

  // ];

  const svgContent2 = `
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
  <path d="M0.5 17.5V14.1154L13.6904 0.930775C13.8416 0.793425 14.0086 0.687292 14.1913 0.612375C14.374 0.537458 14.5656 0.5 14.7661 0.5C14.9666 0.5 15.1608 0.535584 15.3488 0.60675C15.5368 0.677901 15.7032 0.791034 15.848 0.94615L17.0692 2.18268C17.2243 2.32754 17.3349 2.49424 17.4009 2.68278C17.4669 2.87129 17.5 3.05981 17.5 3.24833C17.5 3.44941 17.4656 3.64131 17.3969 3.82403C17.3283 4.00676 17.219 4.17373 17.0692 4.32495L3.88458 17.5H0.5ZM14.5519 4.6942L16 3.25573L14.7442 1.99998L13.3058 3.44805L14.5519 4.6942Z" fill="#00132B"/>
  </svg>
  `;

  const hasNumbers = /\d/.test(name);
  const [helpMessageName, setHelperTextName] = useState("");


  const [helpMessageEmail, setHelperTextEmail] = useState("");

  const isPasswordValid = password.length >= 8;
  const [helpMessagePass, setHelperTextPass] = useState("");


  const handleChangeName = (e) => {
    const inputValue = e.target.value;
    setName(inputValue);
  
    if (!/^[a-zA-Z0-9\s\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FAF]+$/u.test(inputValue)) {
        setHelperTextName(t("manageExAuditorScreenDailogErMessageOneText")); // Prompt to use only Japanese and English characters
    } else if (inputValue.trim() === "") {
        setHelperTextName(t("manageExAuditorScreenDailogErMessageThreeText")); // Prompt to enter a name
    } else if (inputValue.length < 3) {
        setHelperTextName(t("manageAuditSetDailogErMessageTwoText")); // Prompt to enter a name
    } else {
        setHelperTextName(''); // Clear any error message
    }
};



  


  const handleChangeEmail = (e) => {
    const inputValue = e.target.value;
    SetEmail(inputValue);
  
    const isValidEmail = /^\S+@\S+\.\S+$/.test(inputValue);
    const hasSpecialCharacters = /[^\w\s@.-]/.test(inputValue);
  
    if (inputValue.trim() === "") {
      setHelperTextEmail(t("manageExAuditorScreenDailogErMessageFiveText"));
    } else if (!isValidEmail) {
      setHelperTextEmail(t("manageExAuditorScreenDailogErMessageSixText"));
    } else if (hasSpecialCharacters) {
      setHelperTextEmail(t("manageExAuditorScreenDailogErMessageSixText"));
    } else {
      setHelperTextEmail('');
    }
  };
  

  const handleChangePass = (e) => {
    const inputValue = e.target.value;
    SetPassword(inputValue);

    if (inputValue.length < 8) {
      setHelperTextPass(t("manageExAuditorScreenDailogErMessageSevenText"));
    } else {
      // Clear helper text if the entered password is valid
      setHelperTextPass('');
    }
  };


  const handleDeleteOrg = (optionTitle) => {
    const updatedOptions = OrgList.filter(
      (option) => option !== optionTitle
    );
    setOrgList1(updatedOptions);
  };

  const style = {
    "input::-ms-reveal, input::-ms-clear": {
      display: "none"
    }
  };

  return (
    <React.Fragment>
      <button className=" bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full shadow-blue" onClick={handleClickOpen}>
        {t("manageExAuditorsScreenCreateText")}
      </button>


      <Dialog
        open={open}
        onClose={handleClose}
        PaperProps={{
          component: 'form',
          onSubmit: (event) => {
            // event.preventDefault();
            // const formData = new FormData(event.currentTarget);
            // const formJson = Object.fromEntries(formData.entries());
            // const email = formJson.email;
            // console.log(email);
            handleClose();
          },
          style: { width: 350 }
        }}

      >
        <DialogTitle sx={{ fontWeight: "700", color: "black" }}>
          <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between", color: "black", }}>
            <h1>{t("manageExAuditorScreenDailogTitleText")}</h1>
            <CloseIcon onClick={handleClose}></CloseIcon>
          </div>

        </DialogTitle>

        <DialogContent>
          <Divider />
          <br></br>

          <div>
            <div className="ExternalAuditorName" data-content={t("manageExAuditorScreenDailogLabelNameText")}>
            <div className="custom-lable-row">
            <TextField
              // label="Enter Name"
              sx={{ paddingLeft: "10px", paddingRight: "10px" }}
              variant='standard'
              // color="primary"
              value={name}
              InputProps={{
                disableUnderline: true,
              }}
              inputProps={{ maxLength: 40 }}
              placeholder={t("manageExAuditorScreenDailogHintNameText")}
              fullWidth
              onChange={handleChangeName}
              focused
            />
            </div>
            </div>
            {/* Conditionally render helper text */}
            {helpMessageName && (
              <p style={{ color: 'red', fontSize: "12px" }}>
                {helpMessageName}
              </p>
            )}
          </div>

          <br></br>

          <div>
            <div className='ExternalEmailId' data-content={t("manageExAuditorScreenDailogLabelEmailText")}>
            <div className="custom-lable-row">
            <TextField
              // label="Enter Email Id"
              color="primary"
              value={email}
              sx={{ paddingLeft: "10px", paddingRight: "10px" }}
              InputProps={{
                disableUnderline: true,
              }}
              variant='standard'
              inputProps={{ maxLength: 50 }}
              placeholder={t("manageExAuditorScreenDailogHintEmailText")}
              fullWidth
              onChange={handleChangeEmail}
              focused
            />
            </div>
            </div>
            {/* Conditionally render helper text for invalid email */}
            {helpMessageEmail && (
              <p style={{ color: 'red', fontSize: '12px' }}>
                {helpMessageEmail}
              </p>
            )}
          </div>
          <br></br>
          <div>
            <div className='ExternalAuditorPassowrd' data-content={t("manageExAuditorScreenDailogLabelPassword")}>
              <div className="custom-lable-row">
              <GlobalStyles styles={style} />
            <TextField
              // label="Enter Password"
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={handleChangePass}
              sx={{ paddingLeft: "10px", paddingRight: "10px" }}
              variant='standard'
              placeholder={t("manageExAuditorScreenDailogHintPasswordText")}
              inputProps={{
                maxLength: 15,
              }}
              fullWidth
              color="primary"
              focused
              InputProps={{
                disableUnderline: true,
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowPassword}
                      onMouseDown={handleMouseDownPassword}
                      edge="end"
                    >
                      {showPassword ? <Visibility /> : <VisibilityOff />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
            </div>
            </div>
            {helpMessagePass && (
              <p style={{ color: 'red', fontSize: '12px' }}>
                {helpMessagePass}
              </p>
            )}
          </div>

          <br></br>

<div style={{ width: "300px" }}>
        <Autocomplete
          options={OrgList}
          noOptionsText={t("manageExAuditorScreenDailogHintEnterOrg")}
          getOptionLabel={(option) => option}
          onInputChange={(e, newValue) => {
            SetOrg(newValue);
          }}
          renderOption={(params) => {
            // console.log("params ",params);
            return <div
              {...params}
              style={{
                display: "flex",
                width: "100%",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <span>{params.key}</span>
              <p
                onClick={() => {
                  handleDeleteOrg(params.key);
                }}
                style={{ cursor: "pointer" }}
              >
                X
              </p>
            </div>
          }}
          renderInput={(params) => (
            <div className='ExternalOrganazationName' data-content={t("manageExAuditorScreenDailogLabelAddOrgText")}>
               <div className="custom-lable-row">
            <TextField
              {...params}
              // label="Select / Add Organization"
              sx={{ paddingLeft: "10px", paddingRight: "10px" }}
              focused
              InputProps={{ ...params.InputProps, disableUnderline: true }}
              variant="standard"
              placeholder={t("manageExAuditorScreenDailogHintAddOrg")}
              onKeyDown={(e) => {
                if (
                  e.key === "Enter" &&
                  OrgList.findIndex((o) => o === org) === -1
                ) {
                  setOrgList1((o) => [...o, org]);
                }
              }}
            />
            </div>
            </div>
          )}
        />
      </div>

        </DialogContent>
        <br></br>
        <DialogActions>
          {/* <button className=" bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-1 px-4 rounded-full" type="submit" onClick={handleAdd}
            disabled={!true || name.trim() == "" || email.trim() == "" || !email.includes("@") || !email.includes(".") || password == "" || !(7 < password.length && password.length < 16)}
            style={{ marginRight: "20px", marginBottom: "10px", paddingLeft: "20px", paddingRight: "20px", backgroundColor: (!name.trim() || !email.includes("@") || !email.includes(".") || !password.trim() || !(7 < password.length && password.length < 16) || !org) ? 'rgba(0,97,213,0.6)' : '#0061D5' }}>Add</button> */}

          <Button
            variant="contained"
            sx={{ borderRadius: 28, textTransform: "none", main: "#0061D5" }}
            disabled={!true || name.trim() == "" || email.trim() == "" || !email.includes("@") || !email.includes(".") || password == "" || !(7 < password.length && password.length < 16)}
            disableRipple
            onClick={handleAdd}
          >{t("manageExGroupScreenDailogAddMembersButtonText")}
          </Button>
        </DialogActions>
      </Dialog>


      {showSuccessPopup && (
        <SuccessPopUp
          iconColor="primary"
          iconSize={50}
          title={msg == "User Created Successfully" ? "External Auditor Created Successfully" : msg}
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

import * as React from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import MenuItem from "@mui/material/MenuItem";
import { useSelector } from "react-redux";
import { useState } from "react";
import { Divider, IconButton, SvgIcon } from "@mui/material";
import SuccessPopUp from "../../../common/SuccessPopUp/SuccessPopUp";
import ErrorPopUp from "../../../common/ErrorPopUp/ErrorPopUp";
import CloseIcon from "@mui/icons-material/Close";
import { BASE_URL } from "../../../utils/constants";

import EditIcon from "@mui/icons-material/Edit";
import { styled } from "@mui/material/styles";
import Tooltip, { tooltipClasses } from "@mui/material/Tooltip";

import Autocomplete from "@mui/material/Autocomplete";

import "../AuditorsAndGroups.css";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

const LightTooltip = styled(({ className, ...props }) => (
  <Tooltip {...props} classes={{ popper: className }} />
))(({ theme }) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: theme.palette.common.white,
    color: "rgba(0, 0, 0, 0.87)",
    boxShadow: theme.shadows[1],
    fontSize: 11,
  },
}));

export default function EditAuditorConfirm({
  myname,
  myemail,
  myorg,
  onClick,
}) {
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");
  const [email, SetEmail] = useState("");
  const [org, SetOrg] = useState("");
  const [OrgList, setOrgList1] = useState([]);
  const jwtToken = useSelector((state) => state.auth.user.jwtToken);
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [showErrorPopup, setShowErrorPopup] = useState(false);
  const [msg, setMsg] = useState("");
  const axiosPrivate = useAxiosPrivate();

  const {t,i18n} = useTranslation();

  async function handleGetOrgList() {
    try {
      const response = await axiosPrivate.get(
        `${BASE_URL}/box/user/getOrgList`
      );
      // console.log("------->", response.data.status);

      const orgNames = response.data.data.map((item) => item.organizationName);
      setOrgList1(orgNames);
    } catch (error) {
      console.log("I got the below error");
      console.error(error);
    }
  }

  const handleClickOpen = () => {
    setName(myname);
    SetEmail(myemail);
    SetOrg(myorg);
    handleGetOrgList();
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setName("");
    SetEmail("");
    SetOrg("");
    setHelperTextName("");
    setHelperTextEmail("");
  };

  const handleEdit = async () => {
    console.log("name--->ok", name);
    console.log("email--->k", email);
    console.log("org--->k", org);

    const responsebody = {
      name: name,
      userEmail: email,
      organizationName: org,
    };

    try {
      const response = await axiosPrivate.put(
        `${BASE_URL}/box/user/editUser?previous_email_id=${myemail}`,
        responsebody
      );

      console.log("Success ", response.data.message);
      setMsg(response.data.message);

      setShowSuccessPopup(true);
    } catch (error) {
      console.log("Error:", error); // Debugging line

      console.log("Failure ", error.response.data);
      setMsg(error.response.data.message);

      setShowErrorPopup(true);
    }
  };

  const svgContent2 = `
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
  <path d="M0.5 17.5V14.1154L13.6904 0.930775C13.8416 0.793425 14.0086 0.687292 14.1913 0.612375C14.374 0.537458 14.5656 0.5 14.7661 0.5C14.9666 0.5 15.1608 0.535584 15.3488 0.60675C15.5368 0.677901 15.7032 0.791034 15.848 0.94615L17.0692 2.18268C17.2243 2.32754 17.3349 2.49424 17.4009 2.68278C17.4669 2.87129 17.5 3.05981 17.5 3.24833C17.5 3.44941 17.4656 3.64131 17.3969 3.82403C17.3283 4.00676 17.219 4.17373 17.0692 4.32495L3.88458 17.5H0.5ZM14.5519 4.6942L16 3.25573L14.7442 1.99998L13.3058 3.44805L14.5519 4.6942Z" fill="#00132B"/>
  </svg>
  `;

  const hasNumbers = /\d/.test(name);
  const [helpMessageName,setHelperTextName] = useState("");

  const isValidEmail = /^\S+@\S+\.\S+$/.test(email);
  const [helpMessageEmail,setHelperTextEmail] = useState("");




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
  


  const handleDeleteOrg = (optionTitle) => {
    const updatedOptions = OrgList.filter(
      (option) => option !== optionTitle
    );
    setOrgList1(updatedOptions);
  };

  return (
    <React.Fragment>
      {/* <IconButton aria-label="my-icon" onClick={handleClickOpen}>
        <SvgIcon viewBox="0 0 16 18">
          <g dangerouslySetInnerHTML={{ __html: svgContent2 }} />
        </SvgIcon>
      </IconButton> */}
      <Tooltip title={t("manageExAuditorScreenDailogTitleTwoText")} arrow><EditIcon style={{ cursor: 'pointer' }} onClick={handleClickOpen} /></Tooltip>

      <Dialog
        open={open}
        onClose={handleClose}
        PaperProps={{
          component: 'form',
          // onSubmit: (event) => {
          //   event.preventDefault();
          //   const formData = new FormData(event.currentTarget);
          //   const formJson = Object.fromEntries(formData.entries());
          //   const email = formJson.email;
          //   console.log(email);
          //   handleClose();
          // },
        }}
      >

        <DialogTitle sx={{ fontWeight: "700", color: "black" }}>
          <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between" }}>
            <h1>{t("manageExAuditorScreenDailogTitleTwoText")}</h1>
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
        // color="primary"
        value={name}
        sx={{ paddingLeft: "10px", paddingRight: "10px" }}
        variant='standard'
        InputProps={{
          disableUnderline: true,
        }}
        inputProps={{ maxLength: 40 }}
        placeholder="For ex: John"
        fullWidth
        onChange={handleChangeName}
        focused
      />
      </div>
      </div>
      {/* Conditionally render helper text */}
      {helpMessageName && (
        <p style={{ color: 'red', fontSize:"12px" }}>
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
        // color="primary"
        value={email}
        sx={{ paddingLeft: "10px", paddingRight: "10px" }}
        variant='standard'
        InputProps={{
          disableUnderline: true,
        }}
        inputProps={{ maxLength: 50 }}
        placeholder="For ex: John@gmail.com"
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


<div style={{ width: "300px" }}>
        <Autocomplete
          defaultValue={myorg}
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
              focused
              InputProps={{ ...params.InputProps, disableUnderline: true }}
              sx={{ paddingLeft: "10px", paddingRight: "10px" }}
              variant="standard"
              placeholder={t("manageExAuditorScreenDailogHintAddOrg")}
              // onKeyDown={(e) => {
              //   if (
              //     e.key === "Enter" &&
              //     OrgList.findIndex((o) => o === org) === -1
              //   ) {
              //     setOrgList1((o) => [...o, org]);
              //   }
              // }}

              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  const existingOptionIndex = OrgList.findIndex((o) => o === org);
                  if (existingOptionIndex === -1) {
                    // If the entered value is not an existing option, add it to the list
                    setOrgList1((o) => [...o, org]);
                  }
                  // Prevent the default behavior (e.g., form submission)
                  e.preventDefault();
                }
              }}

            />
            </div>
            </div>
          )}
        />
      </div>




        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>{t("manageExAuditorScreenDailogCancelButton")}</Button>
          <Button
            variant="contained"
            sx={{ borderRadius: 28, textTransform: "none", main: "#0061D5" }}
            disableRipple
            onClick={handleEdit}
          >{t("manageExAuditorScreenDailogAddButton")}
          </Button>
        </DialogActions>
      </Dialog>


      {showSuccessPopup && (
        <SuccessPopUp
          iconColor="primary"
          iconSize={50}
          title={msg == "User updated Successfully" ? "External Auditor updated Successfully" : msg}
          open={showSuccessPopup}
          handleClose={() => {
            onClick();
            handleClose();
            setShowSuccessPopup(false);
          }}
        />
      )}


      {showErrorPopup && (
        <ErrorPopUp
          iconColor="error"
          iconSize={50}
          title={msg}
          open={showErrorPopup}
          handleClose={() => {
            onClick();
            setShowErrorPopup(false)
          }}
        />
      )}

    </React.Fragment>
  );
}

import * as React from "react";
import { useSelector } from "react-redux";

import Button from "@mui/material/Button";
import { styled } from "@mui/material/styles";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import TextField from "@mui/material/TextField";
import "../AuditSet.css";

import SuccessPopUp from "../../../common/SuccessPopUp/SuccessPopUp";
import ErrorPopUp from "../../../common/ErrorPopUp/ErrorPopUp";
import EditIcon from "@mui/icons-material/Edit";

import OutlinedInput from "@mui/material/OutlinedInput";
import MenuItem from "@mui/material/MenuItem";

import ListItemText from "@mui/material/ListItemText";
import Select from "@mui/material/Select";
import Checkbox from "@mui/material/Checkbox";
import { InputAdornment } from "@mui/material";
import { BASE_URL } from "../../../utils/constants";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";

import Tooltip, { tooltipClasses } from "@mui/material/Tooltip";

import { useTranslation } from "react-i18next";

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  "& .MuiDialogContent-root": {
    padding: theme.spacing(2),
  },
  "& .MuiDialogActions-root": {
    padding: theme.spacing(1),
  },
}));

/////////////////////////////////////////////////////////
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

export default function CustomizedDialogs({
  selectedAuditId = null,
  selectedOwnerEmail = "",
  selectedAuditName = "",
  selectedAuditDescrption = "",
  getListofAudit,
}) {
  const userEmail = useSelector((state) => state.auth.user.userEmail);
  const [open, setOpen] = React.useState(false);
  const axiosPrivate = useAxiosPrivate();

  const [managedMembers, setSelectedManagedMenbers] = React.useState([]);
  const [selectedCoOwnersList, setCoOwnersList] = React.useState([]);
  const [selectedExternalAuditorList, setSelectedAuditorList] = React.useState(
    []
  );

  const [selectedGroupList, setSelectedGroupList] = React.useState([]);

  const [showSuccessPopup, setShowSuccessPopup] = React.useState(false);
  const [showErrorPopup, setShowErrorPopup] = React.useState(false);

  const [isPrevious, setIsPrevious] = React.useState(false);

  const [isGlobalvar, setGlobalvar] = React.useState(false);

  const [messagePopUp, setMessagePopUp] = React.useState("");

  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);

    if (selectedAuditId === null) {
      setFirstText("");
      setSecondText("");
    } else {
      setFirstText(selectedAuditName);
      setSecondText(selectedAuditDescrption);
    }
    setDailogViewTwo(false);
    setGlobalvar(false);
    setAuditId(null);
    setMessagePopUp("");
    setSelectedManagedMenbers([]);
    setCoOwnersList([]);
    setSelectedAuditorList([]);
    setSelectedGroupList([]);
    setHelperAuditTextName("");
    setHelperAuditTextInfo("");
    setIsPrevious(false);

    setGeneralUsers([]);
    setAuditAdmins([]);
  };

  ////////////////////////////////////////////////////////////////
  // handle text
  const [firsttext, setFirstText] = React.useState(selectedAuditName);
  const [secondtext, setSecondText] = React.useState(selectedAuditDescrption);
  const [disabled, setDisabled] = React.useState(true);
  const [disabledTwo, setDisabledTwo] = React.useState(true);

  const {t,i18n} = useTranslation();

  React.useEffect(() => {
    // Check if all three lists are empty
    const allListsEmpty =
      managedMembers.length === 0 &&
      selectedCoOwnersList.length === 0 &&
      selectedExternalAuditorList.length === 0;
    // Set disabledTwo accordingly
    setDisabledTwo(allListsEmpty);

    if (firsttext !== "" && secondtext !== "") {
      setDisabled(false);
    } else {
      setDisabled(true);
    }
  }, [
    firsttext,
    secondtext,
    managedMembers,
    selectedCoOwnersList,
    selectedExternalAuditorList,
  ]);

  const handleTextFirst = (event) => {
    const inputValue = event.target.value;
    setFirstText(inputValue);

    if (inputValue.trim() === "") {
      // If the input is empty or contains only whitespace, set a helper text message.
      setHelperAuditTextName(t("manageAuditSetDailogErMessageOneText"));
    } else if (inputValue.length < 3) {
      setHelperAuditTextName(t("manageAuditSetDailogErMessageTwoText"));
    } else {
      // If the input is valid, clear any previous helper text.
      setHelperAuditTextName("");
    }
  };

  const handleTextSecond = (event) => {
    const inputValue = event.target.value;
    setSecondText(event.target.value);
    if (inputValue.trim() === "") {
      // If the input is empty or contains only whitespace, set a helper text message.
      setHelperAuditTextInfo(t("manageAuditSetDailogErMessageThreeText"));
    } else {
      // If the input is valid, clear any previous helper text.
      setHelperAuditTextInfo("");
    }
  };

  /////////////////////////////////////////////////////////
  // handle tabs
  const [isDailogViewTwo, setDailogViewTwo] = React.useState(false);

  const createAuditSetsNextButton = () => {
    // setView(!isDailogViewTwo);
    if (firsttext !== "" && secondtext !== "") {
      console.log("first select options ", firsttext, secondtext);

      if (selectedAuditId === null) {
        // createAuditSetsCall();
        getManagedUsers();
        getExternalUsers();
        getAuditGroupsData();
        setDailogViewTwo(true);
      } else {
        console.log("update audit id", selectedAuditId);
        console.log("first select options ", firsttext, secondtext);
        if (
          firsttext == selectedAuditName &&
          secondtext === selectedAuditDescrption
        ) {
          console.log("same copies");
          getManagedUsers();
          getExternalUsers();
          getAuditGroupsData();
          GetCollaboratorsForAuditSet(selectedAuditId);

          setDailogViewTwo(true);
        } else {
          console.log("they are not same");
          // UpdateAuditSetsCall(selectedAuditId);
          getManagedUsers();
          getExternalUsers();
          getAuditGroupsData();
          GetCollaboratorsForAuditSet(selectedAuditId);

          setDailogViewTwo(true);
        }
        // UpdateAuditSetsCall(selectedAuditId);
      }
    } else {
      console.log("this is else");
    }
  };

  ///////////////////////////////////////////////
  /// api calls for creting audit sets

  const [auditId, setAuditId] = React.useState("");

  const createAuditSetsCall = () => {
    const url = `${BASE_URL}/box/auditSet/createAuditSet`;
    
    const requestBody = {
      auditName: firsttext,
      description: secondtext,
    };
    if (isPrevious) {
      setDailogViewTwo(true);
      if (firsttext === originalName && secondtext === originalInfo) {
        console.log("ok");
      } else {
        console.log("not ok");
        const savedName = firsttext;
        const savedInfo = secondtext;

        axiosPrivate
          .post(url, requestBody)
          .then((response) => {
            getManagedUsers();
            getExternalUsers();
            getAuditGroupsData();

            getListofAudit();

            console.log(response.data.message);
            setMessagePopUp(response.data.message);

            setAuditId(response.data.data.auditSetId);
            setDailogViewTwo(true);

            // setShowSuccessPopup(true);
            setIsPrevious(true);
            savePreviousData(savedName, savedInfo);
          })
          .catch((error) => {
            if (error.response && error.response.status === 400) {
              setMessagePopUp(error.response.data.message);

              setShowErrorPopup(true);
            } else {
              // Handle other errors
              setMessagePopUp("Error");

              // setShowErrorPopup(true);
              console.error(error);
            }
          });
      }
    } else {
      const savedName = firsttext;
      const savedInfo = secondtext;

      axiosPrivate
        .post(url, requestBody)
        .then((response) => {
          getManagedUsers();
          getExternalUsers();
          getAuditGroupsData();

          getListofAudit();

          console.log(response.data.message);
          setMessagePopUp(response.data.message);

          setAuditId(response.data.data.auditSetId);
          setDailogViewTwo(true);

          // setShowSuccessPopup(true);
          setIsPrevious(true);
          savePreviousData(savedName, savedInfo);
        })
        .catch((error) => {
          if (error.response && error.response.status === 400) {
            setMessagePopUp(error.response.data.message);

            setShowErrorPopup(true);
          } else {
            // Handle other errors
            setMessagePopUp("Error");

            // setShowErrorPopup(true);
            console.error(error);
          }
        });
    }
  };

  const UpdateAuditSetsCall = (Id) => {
    const url = `${BASE_URL}/box/auditSet/updateAuditSetInfo/${Id}`;
    const requestBody = {
      auditSetName: firsttext,
      description: secondtext,
    };

    axiosPrivate
      .put(url, requestBody)
      .then((response) => {
        getListofAudit();

        getManagedUsers();
        getExternalUsers();
        getAuditGroupsData();

        GetCollaboratorsForAuditSet(Id);

        console.log(response.data.message);
        setMessagePopUp(response.data.message);

        // // setAuditId(response.data.data.auditSetId);
        // setGlobalvar(true);

        // setShowSuccessPopup(true);

        setDailogViewTwo(true);

        // setShowSuccessPopup(true);
      })
      .catch((error) => {
        if (error.response && error.response.status === 400) {
          setMessagePopUp(error.response.data.message);

          setShowErrorPopup(true);
        } else {
          // Handle other errors
          setMessagePopUp("Error");

          // setShowErrorPopup(true);
          console.error(error);
        }
      });
  };

  const AddUserToAudit = (combinedList, auditGroupList) => {
    const url = `${BASE_URL}/box/auditSet/createAuditSet`;
    
    const requestBody = {
      auditName: firsttext,
      description: secondtext,
      auditSetCollab: combinedList,
      grpIds: auditGroupList,
    };

    axiosPrivate
      .post(url, requestBody)
      .then((response) => {
        console.log("this is data", response.status);

        setMessagePopUp(response.data.message);
        setShowSuccessPopup(true);
        getListofAudit();
        setGlobalvar(true);
      })
      .catch((error) => {
        if (error.response && error.response.status === 400) {
          setMessagePopUp(error.response.data.message);

          setShowErrorPopup(true);
        } else {
          // Handle other errors
          console.error(error);
        }
      });
  };

  const UpdateUserToAudit = (
    auditGroupList,
    membersList,
    coOwnerList,
    externalAuditorList
  ) => {
    const url = `${BASE_URL}/box/auditSet/updateAuditSet/${selectedAuditId}`;
    
    // // Assuming you have some data to send in the request body
    // const requestBody = {
    //   "grpIDs": auditGroupList,
    //   "coOwners": coOwnerList,
    //   "members": membersList,
    //   "reviewers": externalAuditorList
    // };

    const requestBody = {
      updateAuditSetInfo: {
        auditSetName: firsttext,
        description: secondtext,
      },
      updateAuditSetCollaborators: {
        grpIDs: auditGroupList,
        coOwners: coOwnerList,
        members: membersList,
        reviewers: externalAuditorList,
      },
    };

    axiosPrivate
      .put(url, requestBody)
      .then((response) => {
        console.log("this is data", response.status);

        setMessagePopUp(response.data.message);
        setShowSuccessPopup(true);
        getListofAudit();
        setGlobalvar(true);
      })
      .catch((error) => {
        if (error.response && error.response.status === 400) {
          setMessagePopUp(error.response.data.message);

          setShowErrorPopup(true);
        } else {
          // Handle other errors
          console.error(error);
        }
      });
  };

  //////////////////////////////////////////////
  /// other get apis for owners

  const [generalUsers, setGeneralUsers] = React.useState([]);
  const [auditAdmins, setAuditAdmins] = React.useState([]);

  /////////////////////////////////////////////////////////

  const [managedUsersList, setManagedUserList] = React.useState([]);
  const [externalAuditorList, setExternalAuditorList] = React.useState([]);
  const [auditGroupData, setAuditGroupData] = React.useState([]);

  const getManagedUsers = () => {
    console.log("owner email and name", userEmail);

    const urlOne = `${BASE_URL}/box/auditSetCollab/getGeneralUserList`;
    const urlTwo = `${BASE_URL}/box/auditSetCollab/getAuditAdminList`;
    
    // Create an array of Axios GET requests
    const requests = [axiosPrivate.get(urlOne), axiosPrivate.get(urlTwo)];

    // Execute all requests concurrently
    Promise.all(requests)
      .then((responses) => {
        // Handle responses for both requests
        const responseDataOne = responses[0].data.data;
        const responseDataTwo = responses[1].data.data;

        const generalUsers = responseDataOne.map((user) => ({
          name: user.name,
          userEmail: user.userEmail,
        }));
        setGeneralUsers(generalUsers);

        if (selectedAuditId === null) {
          const adminList = responseDataTwo
            .filter((user) => user.userEmail !== userEmail)
            .map((user) => ({
              name: user.name,
              userEmail: user.userEmail,
            }));
          setAuditAdmins(adminList);
        } else {
          const adminList = responseDataTwo
            .filter((user) => user.userEmail !== selectedOwnerEmail)
            .map((user) => ({
              name: user.name,
              userEmail: user.userEmail,
            }));
          setAuditAdmins(adminList);
        }

        const ResponseList = responseDataOne.concat(responseDataTwo);

        const userEmails = ResponseList.map((user) => ({
          name: user.name,
          userEmail: user.userEmail,
        }));
        setManagedUserList(userEmails);

        // console.log("Response from urlOne:", generalUsers);
        // console.log("Response from urlTwo:", adminList);
        // console.log("Response from urlthree:", userEmails);

        // Further processing of data...
      })
      .catch((error) => {
        setManagedUserList([]);
        console.error(error);
      });
  };

  const getExternalUsers = () => {
    const url = `${BASE_URL}/box/user/getListOfExternalAuditors`;
    
    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data.data);
        const userEmails = response.data.data.map((user) => ({
          name: user.name,
          userEmail: user.userEmail,
        }));

        setExternalAuditorList(userEmails);
      })
      .catch((error) => {
        setExternalAuditorList([]);
        console.error(error);
      });
  };

  const getAuditGroupsData = () => {
    const url = `${BASE_URL}/box/auditGroup/getListOfAuditGroup`;

    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log("okk",response.data.data);

        const groupsList = response.data.data
          ? response.data.data.map((group) => ({
              auditGroupId: group.auditGroupId,
              auditGroupName: group.auditGroupName,
            }))
          : [];

        // console.log("rather to be ",groupsList);
        setAuditGroupData(groupsList);
      })
      .catch((error) => {
        console.error(error);
      });
  };

  const GetCollaboratorsForAuditSet = (id) => {
    const url = `${BASE_URL}/box/auditSetCollab/getCollaboratorsForAuditSet?auditSetId=${id}`;
    
    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data.data);

        const membersEmails = response.data.data.collaboratorList.members
          ? response.data.data.collaboratorList.members.map(
              (member) => member.emailId
            )
          : [];
        const coOwnersEmails = response.data.data.collaboratorList.coOwners
          ? response.data.data.collaboratorList.coOwners.map(
              (member) => member.emailId
            )
          : [];
        const reviewersEmails = response.data.data.collaboratorList.reviewers
          ? response.data.data.collaboratorList.reviewers.map(
              (reviewer) => reviewer.emailId
            )
          : [];

        const groupsList = response.data.data.auditGroupListForAuditSetList
          ? response.data.data.auditGroupListForAuditSetList.map(
              (group) => group.auditGroupId
            )
          : [];

        // console.log("selected memebers list ",membersEmails);
        // console.log("selected coowners list",coOwnersEmails);
        // console.log("selected reviewers emails",reviewersEmails);
        // console.log("selected groups ",groupsList);

        setSelectedManagedMenbers(membersEmails);
        setCoOwnersList(coOwnersEmails);
        setSelectedAuditorList(reviewersEmails);
        setSelectedGroupList(groupsList);
      })
      .catch((error) => {
        console.error(error);
      });
  };

  ///////////////////////////////////////////
  /// handle dropdown multi select

  const handleChangeMembers = (event) => {
    const {
      target: { value },
    } = event;
    setSelectedManagedMenbers(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };

  const handleChangeCoOwners = (event) => {
    const {
      target: { value },
    } = event;
    setCoOwnersList(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };

  const handleChangeExternalAuditors = (event) => {
    const {
      target: { value },
    } = event;
    setSelectedAuditorList(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };

  const handleChangeGroupList = (event) => {
    const {
      target: { value },
    } = event;
    setSelectedGroupList(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };

  const handleDeleteMembersoption = (index) => {
    const newPersonName = [...managedMembers];
    newPersonName.splice(index, 1);
    setSelectedManagedMenbers(newPersonName);
  };

  const handleDeleteCoOwners = (index) => {
    const newPersonName = [...selectedCoOwnersList];
    newPersonName.splice(index, 1);
    setCoOwnersList(newPersonName);
  };

  const handleDeleteExternalAuditors = (index) => {
    const newPersonName = [...selectedExternalAuditorList];
    newPersonName.splice(index, 1);
    setSelectedAuditorList(newPersonName);
  };

  const handleDeleteGroupList = (index) => {
    const newPersonName = [...selectedGroupList];
    newPersonName.splice(index, 1);
    setSelectedGroupList(newPersonName);
  };

  ////////////////////////////////////////////

  const getOptionLabels = (selectedValues, optionsList) => {
    return selectedValues.map((value) => {
      const selectedOption = optionsList.find(
        (option) => option.userEmail === value
      );
      return selectedOption ? selectedOption.name : "";
    });
  };

  const getOptionsForGroups = (firstList, secondList) => {
    const selectedAuditGroupNames = firstList
      .filter((auditGroup) => secondList.includes(auditGroup.auditGroupId))
      .map((selectedAuditGroup) => selectedAuditGroup.auditGroupName);

    return selectedAuditGroupNames;
  };

  /////
  const [helpMessageAuditName, setHelperAuditTextName] = React.useState("");
  const [helpMessageAuditInfo, setHelperAuditTextInfo] = React.useState("");

  const [originalName, setOriginalName] = React.useState("");
  const [originalInfo, setOriginalInfo] = React.useState("");

  // Function to update originalName and originalInfo when name and info change
  const savePreviousData = (newName, newInfo) => {
    setOriginalName(newName);
    setOriginalInfo(newInfo);
  };

  return (
    <React.Fragment>
      {selectedAuditId == null ? (
        <Button
          variant="contained"
          sx={{ borderRadius: 28, textTransform: "none", main: "#0061D5" }}
          disableRipple
          onClick={handleClickOpen}
        >
          {t("manageAuditSetScreenCreateAduitSetButtonText")}
        </Button>
      ) : (
        <Tooltip title={t("manageAuditSetScreenHoverUpdateText")} arrow>
          <EditIcon style={{ cursor: "pointer" }} onClick={handleClickOpen} />
        </Tooltip>
      )}
      <BootstrapDialog
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        open={open}
        maxWidth="md"
      >
        {!isDailogViewTwo ? (
          /// this is create audit code
          <div>
            <div style={{ height: "48px" }}>
              <DialogTitle
                sx={{ m: 0, p: 2, fontWeight: "bold" }}
                id="customized-dialog-title"
              >
                {selectedAuditId === null ? t("manageAuditSetScreenCreateAduitSetButtonText") : t("manageAuditSetScreenHoverUpdateText")} 
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
            </div>
            <DialogContent>
              <div className="white-line"></div>
              <div style={{ height: "30px", width: "400px" }}></div>

              {/*  */}
              <div className="enter-audit-set-name" data-content={t("manageAuditSetScreenDailogLabelAuditSetName")}>
                <div className="custom-lable-row">
                  <TextField
                    onChange={(event) => handleTextFirst(event)}
                    defaultValue={firsttext}
                    placeholder={t("manageAuditSetScreenDailogHintName")}
                    fullWidth
                    sx={{ paddingLeft: "10px", paddingRight: "10px" }}
                    id="fullWidth"
                    variant="standard"
                    inputProps={{ maxLength: 30 }}
                    InputProps={{
                      disableUnderline: true,
                    }}
                  />
                </div>
              </div>
              {helpMessageAuditName && (
                <p style={{ color: "red", fontSize: "12px" }}>
                  {helpMessageAuditName}
                </p>
              )}
              <div style={{ height: "30px", width: "10px" }}></div>
              {/*  */}
              <div className="enter-info-name" data-content={t("manageAuditSetScreenDailogLabelAuditSetDesc")}>
                <div className="custom-lable-row">
                  <TextField
                    onChange={(event) => handleTextSecond(event)}
                    placeholder={t("manageAuditSetScreenDailogHintDesc")}
                    fullWidth
                    multiline
                    rows={5}
                    defaultValue={secondtext}
                    sx={{ paddingLeft: "10px", paddingRight: "10px" }}
                    id="fullWidth"
                    variant="standard"
                    inputProps={{ maxLength: 100 }}
                    InputProps={{
                      disableUnderline: true,
                      endAdornment: (
                        // <div style={{
                        //   fontSize: "small",
                        //   position: "absolute",
                        //   // top: 0,
                        //   // bottom: 0,
                        //   // right: 0,
                        //   height:"640px",
                        // }}>{secondtext.length}/100</div>

                        <div
                          style={{
                            display: "flex",
                            // backgroundColor: "red",
                            height: "114px",
                            fontSize: "small",
                          }}
                        >
                          {secondtext.length}/100
                        </div>
                      ),
                    }}
                  />
                </div>
              </div>
              {helpMessageAuditInfo && (
                <p style={{ color: "red", fontSize: "12px" }}>
                  {helpMessageAuditInfo}
                </p>
              )}
              <div style={{ height: "20px" }}></div>
            </DialogContent>
            <DialogActions>
              <Button
                autoFocus
                variant="contained"
                sx={{
                  borderRadius: 28,
                  textTransform: "none",
                  main: "#0061D5",
                }}
                disableRipple
                onClick={createAuditSetsNextButton}
                disabled={disabled}
              >
                {selectedAuditId === null ? t("manageAuditSetScreenDailogNextButton") : t("manageAuditSetScreenDailogNextButton")}
              </Button>
            </DialogActions>
          </div>
        ) : (
          /// this is
          <div>
            <DialogTitle
              sx={{ m: 0, p: 2, fontWeight: "bold" }}
              id="customized-dialog-title"
            >
              {selectedAuditId === null ? t("manageAuditSetScreenDailogAddCollaborators") : t("manageAuditSetScreenDailogUpdateCollaborators")} 
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
            <DialogContent>
              <div className="white-line"></div>
              <div style={{ height: "20px", width: "600px" }}></div>
              <div style={{ display: "flex" }}>
                {/* first multi select list */}
                <div class="row">
                  <div>
                    {selectedAuditId === null ? t("manageAuditSetScreenDailogAddMembersText") : t("manageAuditSetScreenDailogUpdateMembersText")} 
                  </div>
                  <div style={{ height: "10px", width: "10px" }}></div>
                  <div className="cusotm-one-layout" data-content={t("manageAuditSetScreenDailogLabelSelectMembersText")}>
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
                        multiple
                        value={managedMembers}
                        onChange={handleChangeMembers}
                        input={
                          <OutlinedInput
                            id="select-multiple-chip"
                            label="Chip"
                          />
                        }
                        displayEmpty
                        renderValue={(selected) => {
                          if (selected.length === 0) {
                            return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                          }
                          return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                        }}
                        MenuProps={MenuProps}
                      >
                        {generalUsers.map((user, index) => (
                          <MenuItem key={index} value={user.userEmail}>
                            <Checkbox
                              checked={
                                managedMembers.indexOf(user.userEmail) > -1
                              }
                            />
                            <ListItemText primary={user.name} />
                          </MenuItem>
                        ))}
                      </Select>
                    </div>
                  </div>
                  <ChipListLayout
                    itemList={getOptionLabels(managedMembers, managedUsersList)}
                    handleDeleteDropdownoption={handleDeleteMembersoption}
                  />
                </div>
                {/* second multi select list*/}
                <div class="row">
                  <p>
                    {selectedAuditId === null ? t("manageAuditSetScreenDailogAddCoownerText") : t("manageAuditSetScreenDailogUpdateCoownerText")} 
                  </p>
                  <div style={{ height: "10px", width: "10px" }}></div>
                  <div className="cusotm-two-layout" data-content={t("manageAuditSetScreenDailogLabelSelectCoOwnersText")}>
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
                        multiple
                        value={selectedCoOwnersList}
                        onChange={handleChangeCoOwners}
                        input={
                          <OutlinedInput
                            id="select-multiple-chip"
                            label="Chip"
                          />
                        }
                        displayEmpty
                        renderValue={(selected) => {
                          if (selected.length === 0) {
                            return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                          }
                          return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                        }}
                        MenuProps={MenuProps}
                      >
                        {auditAdmins.map((user, index) => (
                          <MenuItem key={index} value={user.userEmail}>
                            <Checkbox
                              checked={
                                selectedCoOwnersList.indexOf(user.userEmail) >
                                -1
                              }
                            />
                            <ListItemText primary={user.name} />
                          </MenuItem>
                        ))}
                      </Select>
                    </div>
                  </div>
                  <ChipListLayout
                    itemList={getOptionLabels(
                      selectedCoOwnersList,
                      managedUsersList
                    )}
                    handleDeleteDropdownoption={handleDeleteCoOwners}
                  />
                </div>
              </div>

              {/* end of first row and start second row */}

              <div style={{ display: "flex" }}>
                {/*thyird multi select list*/}
                <div class="row">
                  <p>
                    {selectedAuditId === null ? t("manageAuditSetScreenDailogAddReviewersText") : t("manageAuditSetScreenDailogUpdateReviewersText")} 
                  </p>
                  <div style={{ height: "10px", width: "10px" }}></div>
                  <div className="cusotm-three-layout" data-content={t("manageAuditSetScreenDailogLabelSelectReviewersText")}>
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
                        multiple
                        value={selectedExternalAuditorList}
                        onChange={handleChangeExternalAuditors}
                        input={
                          <OutlinedInput
                            id="select-multiple-chip"
                            label="Chip"
                          />
                        }
                        displayEmpty
                        renderValue={(selected) => {
                          if (selected.length === 0) {
                            return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                          }
                          return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                        }}
                        MenuProps={MenuProps}
                      >
                        {externalAuditorList.map((user, index) => (
                          <MenuItem key={index} value={user.userEmail}>
                            <Checkbox
                              checked={
                                selectedExternalAuditorList.indexOf(
                                  user.userEmail
                                ) > -1
                              }
                            />
                            <ListItemText primary={user.name} />
                          </MenuItem>
                        ))}
                      </Select>
                    </div>
                  </div>
                  <ChipListLayout
                    itemList={getOptionLabels(
                      selectedExternalAuditorList,
                      externalAuditorList
                    )}
                    handleDeleteDropdownoption={handleDeleteExternalAuditors}
                  />
                </div>

                {/* fourth multi select group */}
                <div class="row">
                  <p>
                    {selectedAuditId === null ? t("manageAuditSetScreenDailogAddAuditGroupText") : t("manageAuditSetScreenDailogUpdateAuditGroupText")} 
                  </p>
                  <div style={{ height: "10px", width: "10px" }}></div>
                  <div className="cusotm-four-layout" data-content={t("manageAuditSetScreenDailogLabelSelectAuditGroupText")}>
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
                        multiple
                        value={selectedGroupList}
                        onChange={handleChangeGroupList}
                        input={
                          <OutlinedInput
                            id="select-multiple-chip"
                            label="Chip"
                          />
                        }
                        displayEmpty
                        renderValue={(selected) => {
                          if (selected.length === 0) {
                            return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                          }
                          return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                        }}
                        MenuProps={MenuProps}
                      >
                        {auditGroupData.map((user, index) => (
                          <MenuItem key={index} value={user.auditGroupId}>
                            <Checkbox
                              checked={
                                selectedGroupList.indexOf(user.auditGroupId) >
                                -1
                              }
                            />
                            <ListItemText primary={user.auditGroupName} />
                          </MenuItem>
                        ))}
                      </Select>
                    </div>
                  </div>
                  <ChipListLayout
                    itemList={getOptionsForGroups(
                      auditGroupData,
                      selectedGroupList
                    )}
                    handleDeleteDropdownoption={handleDeleteGroupList}
                  />
                </div>
              </div>
            </DialogContent>
            <DialogActions>
              <Button
                variant="text"
                sx={{ borderRadius: 28, textTransform: "none" }}
                disableRipple
                onClick={(e) => {
                  setDailogViewTwo(!isDailogViewTwo);
                  setGeneralUsers([]);
                  setAuditAdmins([]);
                }}
              >
                {t("manageAuditSetScreenDailogBackButtonText")}
              </Button>
              <Button
                autoFocus
                variant="contained"
                sx={{
                  borderRadius: 28,
                  textTransform: "none",
                  main: "#0061D5",
                }}
                disableRipple
                // disabled={selectedAuditId !== null ? false : disabledTwo}
                onClick={(e) => {
                  // console.log("first list",managedUsersList,externalAuditorList);
                  // console.log("second list",handleUserSelect(selectedCoOwnersList, 'CO_OWNER'));
                  // console.log("third list",handleUserSelect(selectedExternalAuditorList, 'REVIEWER'));

                  const combinedList = [
                    ...handleUserSelect(managedMembers, "MEMBER"),
                    ...handleUserSelect(selectedCoOwnersList, "CO_OWNER"),
                    ...handleUserSelect(
                      selectedExternalAuditorList,
                      "REVIEWER"
                    ),
                  ];

                  // console.log('final list ', combinedList);

                  if (combinedList.length === 0) {
                    console.log("list is empty");
                    if (selectedAuditId !== null) {
                      UpdateUserToAudit(
                        selectedGroupList,
                        getOptionlistForUpdate(
                          managedMembers,
                          managedUsersList,
                          "MEMBER"
                        ),
                        getOptionlistForUpdate(
                          selectedCoOwnersList,
                          managedUsersList,
                          "CO_OWNER"
                        ),
                        getOptionlistForUpdate(
                          selectedExternalAuditorList,
                          externalAuditorList,
                          "REVIEWER"
                        )
                      );
                    } else {
                      AddUserToAudit(combinedList, selectedGroupList);
                    }
                  } else {
                    console.log("list is not empty");
                    if (selectedAuditId === null) {
                      // console.log("create one ",selectedGroupList);
                      AddUserToAudit(combinedList, selectedGroupList);
                    } else {
                      console.log("edit one check");
                      //const editoneList = [getOptionLabels(managedMembers, managedUsersList),getOptionLabels(selectedCoOwnersList, managedUsersList),getOptionLabels(selectedExternalAuditorList, externalAuditorList)];

                      // console.log(
                      // getOptionlistForUpdate(managedMembers, managedUsersList, "MEMBER"),
                      // getOptionlistForUpdate(selectedCoOwnersList, managedUsersList, "CO_OWNER"),
                      // getOptionlistForUpdate(selectedExternalAuditorList, externalAuditorList, "REVIEWER"));

                      UpdateUserToAudit(
                        selectedGroupList,
                        getOptionlistForUpdate(
                          managedMembers,
                          managedUsersList,
                          "MEMBER"
                        ),
                        getOptionlistForUpdate(
                          selectedCoOwnersList,
                          managedUsersList,
                          "CO_OWNER"
                        ),
                        getOptionlistForUpdate(
                          selectedExternalAuditorList,
                          externalAuditorList,
                          "REVIEWER"
                        )
                      );
                    }
                    //
                  }
                }}
              >
                {selectedAuditId === null ? t("manageAuditSetScreenDailogAddButtonText") : t("manageAuditSetScreenDailogUpdateText")}
              </Button>
            </DialogActions>
          </div>
        )}
        <div style={{ height: "10px" }}></div>

        {showSuccessPopup && (
          <SuccessPopUp
            iconColor="primary"
            iconSize={50}
            title={messagePopUp}
            open={showSuccessPopup}
            handleClose={() => {
              setShowSuccessPopup(false);
              if (isGlobalvar) {
                handleClose();
              }
            }}
          />
        )}
        {showErrorPopup && (
          <ErrorPopUp
            iconColor="error"
            iconSize={50}
            title={messagePopUp}
            open={showErrorPopup}
            handleClose={() => setShowErrorPopup(false)}
          />
        )}
      </BootstrapDialog>
    </React.Fragment>
  );
}

const handleUserSelect = (selectedUserEmails, role) => {
  // Adding selected users to auditSetCollab with the specified role
  const newCollabEntries = selectedUserEmails.map((userEmail) => ({
    userEmail: userEmail,
    auditSetRole: role,
  }));

  // Returning the updated array
  return newCollabEntries;
};

const getOptionlistForUpdate = (selectedValues, optionsList, role) => {
  return selectedValues.map((value) => {
    const selectedOption = optionsList.find(
      (option) => option.userEmail === value
    );

    if (selectedOption) {
      return {
        userId: 0, // You may want to assign a unique userId based on your application logic
        userName: selectedOption.name,
        emailId: selectedOption.userEmail,
        role: role,
      };
    } else {
      return null;
    }
  });
};

const ChipListLayout = ({ itemList, handleDeleteDropdownoption }) => {
  return (
    <div style={{ width: "100%" }}>
      <div style={{ height: "10px", width: "10px" }}></div>
      <div className="chip-container">
        {itemList.map((name, index) => (
          <div key={index} className="chip">
            <span>{name} </span>
            <button
              style={{ border: "none", backgroundColor: "transparent" }}
              className="delete-button"
              onClick={() => handleDeleteDropdownoption(index)}
            >
              <span> X </span>
            </button>
          </div>
        ))}
      </div>
      <div style={{ height: "10px", width: "10px" }}></div>
    </div>
  );
};

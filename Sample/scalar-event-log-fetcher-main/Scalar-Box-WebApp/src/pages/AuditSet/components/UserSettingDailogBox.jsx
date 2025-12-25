import * as React from "react";
import Button from "@mui/material/Button";
import { styled } from "@mui/material/styles";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import OutlinedInput from "@mui/material/OutlinedInput";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import ListItemText from "@mui/material/ListItemText";
import Select from "@mui/material/Select";
import Checkbox from "@mui/material/Checkbox";

import "../AuditSet.css";

import { useSelector } from "react-redux";

import UserSettingIcon from "../../../assets/UserSettingSVG";
import { BASE_URL } from "../../../utils/constants";
import SuccessPopUp from "../../../common/SuccessPopUp/SuccessPopUp";
import ErrorPopUp from "../../../common/ErrorPopUp/ErrorPopUp";

import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
import { useTranslation } from "react-i18next";
import Tooltip, { tooltipClasses } from "@mui/material/Tooltip";

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

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  "& .MuiDialogContent-root": {
    padding: theme.spacing(2),
  },
  "& .MuiDialogActions-root": {
    padding: theme.spacing(1),
  },
}));

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

export default function UserSettingDailog({
  selectedAuditId = 0,
  selectedEmail = "",
  getListofAudit,
}) {
  const [open, setOpen] = React.useState(false);

  const [coOwnersList, setCoOwnersList] = React.useState([]);
  const [personName, setPersonName] = React.useState(selectedEmail);

  const [showSuccessPopup, setShowSuccessPopup] = React.useState(false);
  const [showErrorPopup, setShowErrorPopup] = React.useState(false);
  const axiosPrivate = useAxiosPrivate();
  const { t, i18n } = useTranslation();

  const [disabled, setdisabled] = React.useState(true);
  const [message, setMessage] = React.useState("");

  const [isLoaded, setIsLoaded] = React.useState(false);

  const handleChange = (event) => {
    const {
      target: { value },
    } = event;
    setPersonName(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };

  const handleClickOpen = () => {
    setOpen(true);
    GetCollaboratorsForAuditSet(selectedAuditId);
  };
  const handleClose = () => {
    setPersonName(selectedEmail);
    setdisabled(true);
    setOpen(false);
  };

  const GetCollaboratorsForAuditSet = (id) => {
    const url = `${BASE_URL}/box/auditSetCollab/getCollaboratorsForAuditSet?auditSetId=${id}`;

    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data.data);
        // console.log(response.data.data.ownedBy);

        const coOwnersEmails = response.data.data.collaboratorList.coOwners
          ? response.data.data.collaboratorList.coOwners.map(
              (member) => member.emailId
            )
          : [];

        // console.log("coowners list", coOwnersEmails);
        const userEmails = response.data.data.collaboratorList.coOwners.map(
          (user) => ({
            userName: user.userName,
            emailId: user.emailId,
          })
        );
        // console.log(userEmails);
        setCoOwnersList(userEmails);
        setIsLoaded(true);
      })
      .catch((error) => {
        console.error(error);
        setIsLoaded(true);
      });
  };

  const UpdateOwner = (auditId, ownerIdEmail) => {
    const url = `${BASE_URL}/box/auditSetCollab/changeAuditSetOwner?auditSetId=${auditId}&newOwnerId=${ownerIdEmail}`;

    axiosPrivate
      .put(url, null)
      .then((response) => {
        console.log(response.data.message);
        setMessage(response.data.message);
        setShowSuccessPopup(true);
        getListofAudit();
      })
      .catch((error) => {
        setMessage(error.response.data.message);
        setShowErrorPopup(true);
      });
  };

  React.useEffect(() => {
    if (JSON.stringify(coOwnersList) === JSON.stringify([])) {
      //   console.log("list is empty",personName,selectedEmail);
    } else {
      //   console.log("list is not empty");
      //   console.log("list is empty",personName,selectedEmail);

      if (personName === selectedEmail) {
        // console.log("this is");
      } else {
        // console.log("this is not");
        setdisabled(false);
      }
    }
  }, [personName]);

  return (
    <React.Fragment>
      <Tooltip title={t("manageAuditSetScreenChangeOwnerDailogTwoText")} arrow>
        <UserSettingIcon
          style={{ cursor: "pointer" }}
          onClick={handleClickOpen}
        />
      </Tooltip>
      <BootstrapDialog
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        open={open}
      >
        <DialogTitle
          sx={{ m: 0, p: 2, fontWeight: "bold" }}
          id="customized-dialog-title"
        >
          {t("manageAuditSetScreenChangeOwnerDailogText")}
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
          <div style={{ height: "30px", width: "300px" }}></div>

          <div
            className="Change-Owner-css"
            data-content={t("manageAuditSetScreenChangeOwnerDailogLabelText")}
          >
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
                defaultValue={personName}
                value={personName}
                onChange={handleChange}
                input={<OutlinedInput id="select-multiple-chip" label="Chip" />}
                displayEmpty
                renderValue={(selected) => {
                  if (selected.length === 0) {
                    return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                  }
                  return <p>{t("manageAuditSetScreenDailogHintSelect")}</p>;
                }}
                MenuProps={MenuProps}
              >
                <MenuItem value={personName}>
                  {t("manageAuditSetScreenDailogHintSelect")}
                </MenuItem>
                {coOwnersList.map((user) => (
                  <MenuItem key={user.emailId} value={user.emailId}>
                    {user.userName}{" "}
                  </MenuItem>
                ))}
              </Select>
            </div>
          </div>

          <ChipListLayout selectedName={personName} />

          <div style={{ height: "10px" }}></div>
          {isLoaded && (
            <div>
              {coOwnersList.length === 0 ? (
                <p style={{ color: "red", fontSize: "small" }}>
                  {t("manageAuditSetScreenChangeOwnerEmptyList")}
                </p>
              ) : null}
              {/* Render your content when coOwnersList has items */}
              <div>{/* Your content here */}</div>
            </div>
          )}
        </DialogContent>
        <DialogActions>
          <Button
            variant="contained"
            sx={{
              width: "100px",
              borderRadius: 28,
              textTransform: "none",
              main: "#0061D5",
            }}
            disableRipple
            disabled={disabled}
            onClick={() => {
              // console.log(selectedAuditId, personName, selectedEmail);

              UpdateOwner(selectedAuditId, personName);
            }}
          >
            {t("manageAuditSetScreenChangeOwnerApplyButton")}
          </Button>
        </DialogActions>
        <div style={{ height: "10px" }}></div>
      </BootstrapDialog>
      {showSuccessPopup && (
        <SuccessPopUp
          iconColor="primary"
          iconSize={50}
          title={message}
          open={showSuccessPopup}
          handleClose={() => {
            setShowSuccessPopup(false);

            handleClose();
          }}
        />
      )}
      {showErrorPopup && (
        <ErrorPopUp
          iconColor="error"
          iconSize={50}
          title={message}
          open={showErrorPopup}
          handleClose={() => setShowErrorPopup(false)}
        />
      )}
    </React.Fragment>
  );
}

const ChipListLayout = ({ selectedName }) => {
  return (
    <div>
      {selectedName === "" ? (
        <div></div>
      ) : (
        <div>
          <div style={{ height: "10px", width: "10px" }}></div>
          <div className="chip-container">
            <div className="chip">
              <span>{selectedName} </span>
            </div>
          </div>
          <div style={{ height: "10px", width: "10px" }}></div>
        </div>
      )}
    </div>
  );
};

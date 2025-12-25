import * as React from "react";
import PropTypes from "prop-types";
import Button from "@mui/material/Button";
import DialogTitle from "@mui/material/DialogTitle";
import Dialog from "@mui/material/Dialog";
import BuildOutlinedIcon from "@mui/icons-material/BuildOutlined";
import CloseIcon from "../../assets/CloseIconSvg";
import { DialogContent, Divider, IconButton } from "@mui/material";
import {
  EVENT_HISTORY,
  FILE_COPIES,
  VERSION_HISTORY,
} from "../../utils/constants";

import { useTranslation } from "react-i18next";

function SimpleDialog(props) {
  const { onClose, open } = props;

  const handleListItemClick = (value) => {
    onClose(value);
  };

  const {t,i18n} = useTranslation();

  return (
    <Dialog
      maxWidth="md"
      onClose={() => {
        handleListItemClick(null);
      }}
      open={open}
    >
      <DialogContent sx={{ flexDirection: "column", justifyContent: "center" }}>
        <div style={{ width: "250px" }}></div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            gap: "3",
          }}
        >
          <DialogTitle
            sx={{
              paddingBottom: "0px",
              paddingLeft: "2px",
              paddingTop: "0px",
            }}
          >
            {t("externalAuditorScreenPropertiesText")}
          </DialogTitle>

          <IconButton
            aria-label="close"
            onClick={() => {
              handleListItemClick(null);
            }}
            sx={{
              color: (theme) => theme.palette.grey[500],
            }}
          >
            <CloseIcon />
          </IconButton>
        </div>

        <div
          style={{
            alignItems: "center",
          }}
        >
          <Divider />

          <Button
            sx={{
              width: "100%",
              fontSize: "18px",
              fontFamily: "700",
              padding: "10px",
              textTransform: "none",
            }}
            autoFocus
            onClick={() => handleListItemClick(VERSION_HISTORY)}
          >
            {t("viewItemsUnderAuditScreenSubVersionHistoryButtonText")}
          </Button>
          <Divider />

          <Button
            sx={{
              width: "100%",
              fontSize: "18px",
              fontFamily: "700",
              padding: "10px",
              textTransform: "none",
            }}
            autoFocus
            onClick={() => handleListItemClick(FILE_COPIES)}
          >
            {t("viewItemsUnderAuditScreenSubFileCopiesButtonText")}
          </Button>
          <Divider />
          <Button
            sx={{
              width: "100%",
              fontSize: "18px",
              fontFamily: "700",
              padding: "10px",
              textTransform: "none",
            }}
            autoFocus
            onClick={() => handleListItemClick(EVENT_HISTORY)}
          >
            {t("viewItemsUnderAuditScreenSubEventHistoryButtonText")}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}

SimpleDialog.propTypes = {
  onClose: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
};

export default function OpenPropertiesDialog({ handleSelectedOption }) {
  const [open, setOpen] = React.useState(false);

  const {t,i18n} = useTranslation();

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = (value) => {
    setOpen(false);
    handleSelectedOption(value);
  };

  return (
    <div>
      <Button
        onClick={handleClickOpen}
        variant="contained"
        sx={{ backgroundColor: "rgba(51, 66, 85, 1)" }}
        startIcon={<BuildOutlinedIcon />}
      >
       {t("externalAuditorScreenPropertiesText")}
      </Button>
      <SimpleDialog open={open} onClose={handleClose} />
    </div>
  );
}

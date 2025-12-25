/* eslint-disable react/prop-types */

import React from "react";
import { styled } from "@mui/material/styles";
import Tooltip, { tooltipClasses } from "@mui/material/Tooltip";
const NoMaxWidthTooltip = styled(({ className, ...props }) => (
  <Tooltip {...props} classes={{ popper: className }} />
))({
  [`& .${tooltipClasses.tooltip}`]: {
    maxWidth: "none",
  },
});

function PathToolTip({ text = "", title, hideTitle = false, fontSize = 18 }) {
  return (
    <>
      <p style={{ fontSize: `${fontSize}px`, marginRight: "10px" }}>
        {!hideTitle && <b>{title} :</b>}{" "}
        {text.length <= 20 ? (
          text.substring(0, 20)
        ) : (
          <>
            {text.substring(0, 20)}
            <NoMaxWidthTooltip title={text} placement="right-start">
              <span>...</span>
            </NoMaxWidthTooltip>
          </>
        )}
      </p>
    </>
  );
}

export default PathToolTip;

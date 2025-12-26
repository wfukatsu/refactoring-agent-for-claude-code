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

function PathToolTip({ text = "" }) {
  return (
    <>
      <p style={{ fontSize: "16px", marginRight: "10px" }}>
        {text.length <= 30 ? (
          text.substring(0, 30)
        ) : (
          <>
            {text.substring(0, 30)}
            <NoMaxWidthTooltip title={text} placement="bottom">
              <span>...</span>
            </NoMaxWidthTooltip>
          </>
        )}
      </p>
    </>
  );
}

export default PathToolTip;

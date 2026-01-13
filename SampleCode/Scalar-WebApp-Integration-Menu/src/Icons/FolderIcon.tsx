import React from "react";
import { Box, SvgIcon, SvgIconProps } from "@mui/material";

export interface FolderIconProps extends SvgIconProps {}

const FolderIcon = (props: FolderIconProps) => {
  return (
    <SvgIcon {...props}><path d="M2.91 22.833c-.597 0-1.102-.252-1.516-.758-.414-.505-.62-1.123-.62-1.853V3.778c0-.73.206-1.347.62-1.853.414-.505.919-.758 1.516-.758h5.604c.285 0 .559.068.822.203.263.135.491.321.685.558l1.741 2.128h9.33c.597 0 1.102.253 1.515.758.414.506.621 1.123.621 1.853v13.555c0 .73-.207 1.348-.62 1.853-.414.506-.92.758-1.516.758H2.91Z"/></SvgIcon>
  );
};

export default FolderIcon;
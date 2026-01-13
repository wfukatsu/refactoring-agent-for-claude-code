import React from "react";
import { Box, SvgIcon, SvgIconProps } from "@mui/material";

export interface FileIconProps extends SvgIconProps {}

const FileIcon = (props: FileIconProps) => {
  return (
    <SvgIcon {...props}><path d="M3.778 23.227c-.73 0-1.347-.207-1.853-.62-.506-.414-.758-.92-.758-1.516V2.909c0-.597.252-1.102.758-1.516.506-.413 1.123-.62 1.853-.62h10.391c.349 0 .683.055 1.005.166.32.11.6.263.837.457l6.061 4.959c.237.193.423.422.559.685.135.263.202.537.202.821v13.23c0 .597-.252 1.102-.758 1.516-.506.413-1.123.62-1.853.62H3.778ZM14.167 6.795c0 .305.124.56.373.763.25.204.56.306.932.306h5.195l-6.5-5.319v4.25Z"/></SvgIcon>
  );
};

export default FileIcon;